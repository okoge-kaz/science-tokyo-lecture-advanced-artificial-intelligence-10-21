package jssf.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Detects a charset for a text file or stream.
 * The recognizable charsets are SJIS, EUC, UTF8, JIS, and ASCII.
 * <p>
 * テキストファイルを読込む際，ユーザが文字コードを指定しなければ，
 * JavaはOSのデフォルトの文字コードでファイルを処理し，Javaの内部コードであるUTF-8に変換します．
 * 書き出すときにはUTF-8からOSのデフォルトの文字コードに変換して書き出します．
 * この動作は，異なるOS上で作られたテキストファイルを扱う場合に問題となります．
 * 文字化けを防ぐには，読込み前にファイルの文字コードを判別し，
 * 判別された文字コードをInputStreamReaderに指定する処理が必要です．
 * <p>
 * The detection of this algorithm is carried out by utilizing
 * the gap of the occurence ranges of the charsets in ASCII code.
 * 
 * @see java.io.InputStreamReader
 * @see java.nio.charset.Charset
 * @since 2
 * @author hmkz
 */
public class TCCharsetDetector implements Serializable {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	/*
	 * I know that my algorithm cannot distinguish SJIS, MS932, and MS943 properly.
	 * However, since the operating systems used in Ono Lab. are only Windows, Fedora，and CentOS,
	 * the capability is enough to detect any kind of files they created.
	 */
	public static final Charset SJIS = Charset.isSupported("MS932") ? Charset.forName("MS932") : Charset.forName("Shift_JIS");

	public static final Charset EUC = Charset.forName("EUC-JP");

	public static final Charset UTF8 = Charset.forName("UTF8");

	public static final Charset JIS = Charset.isSupported("MS932") ? Charset.forName("MS932") : Charset.forName("JIS");

	public static final Charset ASCII = Charset.forName("ASCII");

	public static final Charset UNKNOWN = null;

	/** ファイルから何バイト読み込んで判別に利用するか */
	private int fBytesForCheck;
	
	/**
	 * ファイルの先頭bytesForCheckバイトを利用して文字コードを判別する判別器を返す．
	 * @param bytesForCheck 判別に利用するバイト数
	 * @since 2 hmkz
	 */
	public TCCharsetDetector(int bytesForCheck) {
		fBytesForCheck = bytesForCheck;
	}

	/**
	 * ファイルの先頭6144バイトを利用して文字コードを判別する判別器を返す．
	 * @since 2 hmkz
	 */
	public TCCharsetDetector() {
		this(6144);	// 2バイト文字と3バイト文字の可能性があるので6の倍数．
	}
	
	/**
	 * ファイルの文字コードを判定し，判定されたコードの文字セットを返す．
	 * 判別できる文字コードはSJIS, EUC, UTF8, JIS, ASCIIであり，
	 * それ以外の文字コードだと判定された場合にはnullを返す．
	 * @param is 文字コードの判定を受ける入力ストリーム．
	 * @return ファイルのエンコーディングに使われている文字セット．
	 * @throws FileNotFoundException ファイルfilenameが存在しないとき．
	 * @throws IOException ファイルの読み込み中にエラーが発生したとき．
	 * @since 2 hmkz
	 */
	public Charset detectCharset(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		// 最初のアスキーコードは読みとばす(ただしJISエスケープコードはとばさない)
		int ch;
		do {
			ch = bis.read();
		} while ((ch <= 0x7f && ch != -1) && ch != 0x1b);

		int noOfNonASCIIBytes = 0;
		int[] nonASCIIBytes = new int[fBytesForCheck];	// 読み込んだバイト列のうち非ASCIIバイトが格納される
		while (ch != -1 && noOfNonASCIIBytes < fBytesForCheck) {
			nonASCIIBytes[noOfNonASCIIBytes] = ch;
			noOfNonASCIIBytes++;
			ch = bis.read();
		}
		if (noOfNonASCIIBytes == 0) {	// バイト列はASCIIコードだけから成る
			return ASCII;
		}

		boolean isFirstChar = true;
		int noOfKanjiBytes = 0;
		int[] kanjiBytes = new int[noOfNonASCIIBytes];	// 非ASCIIバイトから漢字のみを取り出したものが格納される
		for (int i = 0; noOfKanjiBytes < noOfNonASCIIBytes && i < noOfNonASCIIBytes; i++) {
			if (isFirstChar && ((nonASCIIBytes[i] <= 0x7f) && (nonASCIIBytes[i] != 0x1b))) {
				continue;
			}
			if (nonASCIIBytes[i] == 0x1b) { // JISエスケープコードが見付かったらJISと判定して終わる
				return JIS;
			}
			if (isFirstChar && (0x80 <= nonASCIIBytes[i]) && (nonASCIIBytes[i] <= 0x9f)) {
				// 最初に出くわした非ASCIIバイトがこの範囲ならSJISと判定
				return SJIS;
			}
			if (isFirstChar
					&& (((0xa1 <= nonASCIIBytes[i]) && (nonASCIIBytes[i] < 0xe0))
							|| ((0xfc < nonASCIIBytes[i]) && (nonASCIIBytes[i] < 0xff)))) {
				// 最初に出くわした非ASCIIバイトがこの範囲ならEUCと判定
				return EUC;
			} else {
				kanjiBytes[noOfKanjiBytes] = nonASCIIBytes[i];
				noOfKanjiBytes++;
				isFirstChar = false;
			}
		}

		// UTF-8: [00-7F] or [c0-df][80-bf] or [e0-ef][80-bf][80-bf]
		// 最初の漢字の第1バイトはすでにチェックされている．
		// UTF8ならば次のfor文をbreakしないはず．
		boolean isUTF = false;
		for (int i = 0; i < noOfKanjiBytes - 2; ) {
			if (0xe0 <= kanjiBytes[i]&& kanjiBytes[i] <= 0xef) {
				if (0x80 <= kanjiBytes[i + 1] && kanjiBytes[i + 1] <= 0xbf) {
					if (0x80 <= kanjiBytes[i + 2]&& kanjiBytes[i + 2] <= 0xbf) {
						isUTF = true; // UTF 3バイトパターン
						i += 3;
					} else {
						isUTF = false;
						break;
					}
				} else {
					isUTF = false;
					break;
				}
			} else if (0xc0 <= kanjiBytes[i]&& kanjiBytes[i] <= 0xdf) {
				if (0x80 <= kanjiBytes[i + 1] && kanjiBytes[i + 1] <= 0xbf) {
					isUTF = true; // UTF 2バイトパターン
					i += 2;
				} else {
					isUTF = false;
					break;
				}
			} else {
				i += 1;
			}
		}
		if (isUTF) {
			return UTF8;
		}

		// 以下UTFでない，従って漢字はSJISまたはEUCの2バイト文字として第1,2バイト部分を全てチェックする.
		// 今度はSJISまたはEUCでない条件をみていく
		// EUC: [a1-ff][a1-ff]
		boolean isEUC = true;
		for (int i = 0; i < noOfKanjiBytes; ) {
			if (kanjiBytes[i] <= 0x7f) {
				i += 1;
				continue;
			}
			if ((kanjiBytes[i] <= 0x9f) || (kanjiBytes[i + 1] <= 0x9f)) {
				isEUC = false;
				break;
			} else {
				i += 2;
			}
		}
		// SJIS第1バイト: [80-9f], [e0-ea], [ed-fc]
		// SJIS第2バイト: [40-fe]
		boolean isSJIS = true;
		for (int i = 0; i < noOfKanjiBytes; ) {
			if (kanjiBytes[i] <= 0x7f) {
				i += 1;
				continue;
			}
			if ((0xa1 <= kanjiBytes[i] && kanjiBytes[i] < 0xe0)
					|| (kanjiBytes[i] > 0xfc)) {
				isSJIS = false;
				break;
			} else {
				i += 2;
			}
		}

		// ここから先は確率的判断となりますが，通常の日本語文書は必ず平かなをふくみ，
		// 平かなのSJISコードとEUCコードとは重複しませんから，「EUCでない」という判断はかなり確実にできます．
		if (!isEUC) {	// EUCでない判断を優先
			return SJIS;
		} else if (!isSJIS) {
			return EUC;	
		} else if (isEUC && isSJIS) {
			return EUC;	// SJIS第二水準部分が重複しているが判定に困るほど第二水準が続出することはないだろう
		} else {
			return UNKNOWN;
		}		
	}

	/**
	 * ファイルの文字コードを判定し，判定されたコードの文字セットを返す．
	 * 判別できる文字コードはSJIS, EUC, UTF8, JIS, ASCIIであり，
	 * それ以外の文字コードだと判定された場合にはnullを返す．
	 * @param filename 文字コードの判定を受けるファイルのパス．
	 * @return ファイルのエンコーディングに使われている文字セット．
	 * @throws FileNotFoundException ファイルfilenameが存在しないとき．
	 * @throws IOException ファイルの読み込み中にエラーが発生したとき．
	 * @since 2 hmkz
	 */
	public Charset detectCharset(String filename) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(filename);
		Charset result = detectCharset(fis);
		fis.close();
		return result;
	}

}
