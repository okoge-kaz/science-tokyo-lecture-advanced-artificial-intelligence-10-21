package jssf.util;

import java.io.Serializable;
import jssf.random.ICRandom;
import jssf.di.ACParam;

/**
 * ルーレット
 * @since 2
 * @author isao
 */
public class TCRoulette implements Serializable {

	private static final long serialVersionUID = 1L;

	/** スロットの数 */
	private int fNoOfSlots;

	/** 現在のスロットの位置 */
	private int fCurrentSlotIndex;

	/** スロット */
	private double[] fSlots;

	private ICRandom fRandom;

	/**
	 * コンストラクタ
	 * @param noOfSlots the number of slots in the roulette wheel
	 * @param random a random number generator
	 * @since 2 isao
	 */
	public TCRoulette(
			@ACParam(key = "NoOfSlots") int noOfSlots,
			@ACParam(key = "Random") ICRandom random
	) {
		fRandom = random;
		fCurrentSlotIndex = 0;
		setNoOfSlots(0);
	}

	/**
	 * コンストラクタ
	 * @param random a random number generator
	 * @since 2 isao
	 */
	public TCRoulette(ICRandom random) {
		this(0, random);
	}

	/**
	 * ルーレットsrcのパラメータをコピーする．
	 * @param src コピー元のルーレット
	 * @since 2 isao
	 */
	public void copyFrom(TCRoulette src) {
		setNoOfSlots(src.fNoOfSlots);
		fCurrentSlotIndex = src.fCurrentSlotIndex;
		for(int i = 0; i < fNoOfSlots; i++)
			fSlots[i] = src.fSlots[i];
	}

	/**
	 * スロット数をセットする．
	 * @param noOfSlots スロット数
	 * @since 2 isao
	 */
	public void setNoOfSlots(int noOfSlots) {
		resetCurrentSlotIndex();
		if(fNoOfSlots >= noOfSlots) {
			return;
		}
		fSlots = new double[noOfSlots];
		fNoOfSlots = noOfSlots;
	}

	/**
	 * スロット数を返す．
	 * @return スロット数
	 * @since 2 isao
	 */
	public int getNoOfSlots() {
		return fNoOfSlots;
	}

	/**
	 * 現在のスロットの位置をリセットする．<BR>
	 * カレントスロットを0にする．
	 * @since 2 isao
	 */
	public void resetCurrentSlotIndex() {
		fCurrentSlotIndex = 0;
	}

	/**
	 * 現在のスロットの位置を返す．
	 * @return 現在のスロットの位置
	 * @since 2 isao
	 */
	public int getCurrentSlotIndex() {
		return fCurrentSlotIndex;
	}

	/**
	 * スロットに値をセットする．
	 * @param value セットする値
	 * @since 2 isao
	 */
	public void setValueToSlot(double value) {
		if(fCurrentSlotIndex == 0) {
			fSlots[fCurrentSlotIndex] = value;
		} else{
			fSlots[fCurrentSlotIndex] = fSlots[fCurrentSlotIndex-1] + value;
		}
		fCurrentSlotIndex++;
	}

	/**
	 * index番目のスロットの値を返す．
	 * @param index 値を得たいスロットの位置
	 * @return スロットの値
	 * @since 2 isao
	 */
	public double getSlotValue(int index) {
		return fSlots[index];
	}

	/**
	 * ルーレットを回して，ランダムに番号を返す
	 * @return 選ばれた番号
	 * @since 2 isao
	 */
	public int doIt() {
		int  selectedIndex;
		double r = fRandom.nextDouble(0.0, fSlots[fCurrentSlotIndex-1]);
		for (selectedIndex = 0; selectedIndex < fNoOfSlots; selectedIndex++) {
			if (fSlots[selectedIndex] > r) {
				return selectedIndex;
			}
		}
		return selectedIndex;
	}
}
