package jgoal.ga.reproduction;

import java.io.Serializable;
import jgoal.solution.ICRealSolution;
import jgoal.solution.TCSolutionSet;
import jssf.di.ACParam;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;

/**
 * 交叉Rex
 * @author uemura, isao
 *
 * @param <X>
 */
public class TCRex<X extends ICRealSolution> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 利用する確率分布の形状 */
	public enum ProbabilityDistribution {
		NORMAL,
		UNIFORM,
	}
	
	private ProbabilityDistribution fProbabilityDistribution;
	
	/** 利用する確率分布の拡張率．統計量遺伝を満たすように設定される． */
	private double fExpansionRatio;
	
	/** 親個体の重心ベクトル */
	private TCMatrix fXg;
	
	private TCMatrix fWork;
	
	private int fNoOfParents;

	private ICRandom fRandom;
	
	/**
	 * コンストラクタ
	 * @param dim 問題の次元数
	 * @param noOfParents 親個体数
	 * @param pd 利用する確率分布．NORMALもしくはUNIFORMを選択．
	 * @param random 乱数生成器
	 */
	public TCRex(
			@ACParam(key="ProblemDimension") int dim,
			@ACParam(key="NoOfParents") int noOfParents,
			@ACParam(key="ProbabilityDistribution") ProbabilityDistribution pd,
			@ACParam(key="Random", defaultValue="$Random") ICRandom random
	) {
		fNoOfParents = noOfParents;
		fProbabilityDistribution = pd;
		fRandom = random;
		switch(fProbabilityDistribution) {
		case NORMAL:
			fExpansionRatio = Math.sqrt(1.0 / (double)fNoOfParents);
			break;
		case UNIFORM:
			fExpansionRatio = Math.sqrt(3.0 / (double)fNoOfParents);
			break;
		}
		fXg = new TCMatrix(dim, 1);
		fWork = new TCMatrix(dim, 1);
	}
	
	/**
	 * 親個体数を(n+1)で初期化．
	 * 
	 * @param dim 問題の次元数
	 * @param pd 利用する確率分布．NORMALもしくはUNIFORMを選択．
	 * @param random 乱数生成器
	 */
	public TCRex(
			@ACParam(key="ProblemDimension") int dim,
			@ACParam(key="ProbabilityDistribution") ProbabilityDistribution pd,
			@ACParam(key="Random", defaultValue="$Random") ICRandom random
	) {
		this(dim, dim + 1, pd, random);
	}

	public int getNoOfParents() {
		return fNoOfParents;
	}

	public void makeOffspring(TCSolutionSet<X> parents, int noOfKids, TCSolutionSet<X> kids) {
		assert parents.size()  == fNoOfParents;
		calcXg(parents);
		kids.clear();
		kids.resize(noOfKids);
		for(X kid : kids) {
			TCMatrix kVec = kid.getVector();
			kVec.copyFrom(fXg); //重心ベクトルをコピー
			for(X parent : parents) {
				fWork.copyFrom(parent.getVector());
				fWork.sub(fXg);
				double r = 0;
				switch (fProbabilityDistribution) {
				case UNIFORM:
					r = fRandom.nextDouble(-fExpansionRatio, fExpansionRatio);
					break;
				case NORMAL:
					r = fRandom.nextGaussian(0.0, fExpansionRatio);
					break;
				}
				fWork.times(r);
				kVec.add(fWork);
			}
		}
	}
	
	/**
	 * 親個体の重心の計算．
	 * @param parents
	 */
	private void calcXg(TCSolutionSet<X> parents) {
		fXg.setDimensions(parents.get(0).getVector().getRowDimension(), 1);
		fXg.fill(0.0);
		for(X parent : parents) {
			fXg.add(parent.getVector());
		}
		fXg.times(1.0 / (double)parents.size());
	}
	

}
