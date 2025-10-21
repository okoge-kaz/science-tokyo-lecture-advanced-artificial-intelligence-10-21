package jssf.random;

import java.util.Random;

import jssf.di.ACParam;

/**
 * Java SDKのjava.util.Randomクラスを使った乱数生成器．
 *
 * @since 2
 * @author hmkz
 */
public class TCJava48BitLcg extends TCAbstractRandom {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	private Random fRandom;

	/**
	 * デフォルトコンストラクタ．
	 * 乱数クラスとして標準ライブラリのRandom，乱数シードとしてシステム時刻を用いる．
	 */
	public TCJava48BitLcg() {
		fRandom = new Random();
	}

	/**
	 * コンストラクタ
	 * @param seed 乱数シード
	 */
	public TCJava48BitLcg(
			@ACParam(key = "Seed") long seed
	) {
		fRandom = new Random(seed);
	}

	@Override
	protected int next(int bits) {
		assert 0 < bits && bits <= 32;
		return fRandom.nextInt() >>> (32 - bits);
	}

	@Override
	protected void resetSeed(long seed) {
		fRandom.setSeed(seed);
	}

}
