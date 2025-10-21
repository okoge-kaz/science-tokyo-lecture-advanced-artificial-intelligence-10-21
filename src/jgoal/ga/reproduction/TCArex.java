package jgoal.ga.reproduction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import jgoal.solution.ICRealSolution;
import jgoal.solution.TCSolutionFactory;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.comparator.ICComparator;
import jssf.di.ACParam;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;

/**
 * AREXのクラス．
 * 詳細は[秋本 09]を参照．
 * 
 * @author uemura, isao
 */
public class TCArex<X extends ICRealSolution> implements Serializable {

	private class KidAndEpsilon {

		double[] fEpsilon;
		
		X fKid;
		
		public KidAndEpsilon(TCSolutionFactory<X> factory, int noOfParents) {
			fEpsilon = new double[noOfParents];
			fKid = factory.create();
		}
	}
	
	private class KidAndEpsilonSet extends ArrayList<KidAndEpsilon> {
		
		private static final long serialVersionUID = 1L;
		
		TCSolutionFactory<X> fFactory;
		
		int fNoOfParents;
		
		public KidAndEpsilonSet(X template, int noOfParents) {
			fFactory = new TCSolutionFactory<X>(template);
			fNoOfParents = noOfParents;
		}

		public void resize(int size) {
			assert size >= 0;
			int diff = size - size();
			if(diff > 0) {
				do {
					add(new KidAndEpsilon(fFactory, fNoOfParents));
					diff--;
				} while (diff > 0);
			} else {
				do {
					remove(size() - 1);
					diff++;
				} while (diff < 0);
			}
			assert size == size();
		}
	}
	
	/** for serialization */
	public static final long serialVersionUID = 1L;
	
	/** 交叉に用いる親数 */
	private int fMu;
		
	/** 乱数の標準偏差 */
	private double fSigma;
	
	/** 拡張率 */
	private double fAlpha;
	
	/** 学習率 */
	private double fCa;
	
	/** 利用する子個体数 */
	private int fMuA;
	
	/** 内部パラメータLcdp */
	private double fLcdp;
	
	/** 内部パラメータLavg */
	private double fLavg;
	
	/** 子個体中心降下ベクトル */
	private TCMatrix fDescentVector;
	
	/** 親個体の中心ベクトル */
	private TCMatrix fMeanOfParents;
	
	/** 作業用ベクトル */
	private TCMatrix fTmp;
	
	/** 子個体とその子個体生成に用いた乱数のセット */
	private KidAndEpsilonSet fKidAndEpsilonSet;

	/** 個体比較器 */
	private final ICComparator<X> fComparator;
	
	/** 子個体+乱数の比較器 */
	private Comparator<KidAndEpsilon> fKidsSetComparator;
	
	/** 乱数生成器 */
	private ICRandom fRandom;
	
	/**
	 * コンストラクタ
	 * 推奨パラメータで初期化
	 * @param dim 問題次元
	 * @param comparator 個体比較器
	 * @param random 乱数生成器
	 */
	public TCArex(
			@ACParam(key="SolutionTemplate") X solutionTemplate,
			@ACParam(key="ProblemDimension") int dim,
			@ACParam(key="Comparator", defaultValue="$Comparator") ICComparator<X> comparator,
			@ACParam(key="Random", defaultValue="$Random") ICRandom random
	) {
		this(solutionTemplate, dim, dim + 1, 1.0 / (double)dim, 1.0, 1.0 / (5.0 * dim), dim + 1, comparator, random);
	}
	
	/**
	 * コンストラクタ
	 * @param solutionTemplate 解テンプレート
	 * @param dim 問題次元
	 * @param mu 親個体数
	 * @param variance REXで用いる分散
	 * @param initAlpha 初期拡張率
	 * @param ca 学習率
	 * @param muAlpha 拡張率の計算に利用する子個体数
	 * @param comparator 個体比較器
	 * @param random 乱数生成器
	 */
	public TCArex(
			@ACParam(key="SolutionTemplate") X solutionTemplate,
			@ACParam(key="ProblemDimension")int dim,
			@ACParam(key="NoOfParents")int mu,
			@ACParam(key="Variance")double variance,
			@ACParam(key="InitialExpansionRate")double initAlpha,
			@ACParam(key="LearningRate")double ca,
			@ACParam(key="SelectionMass")int muAlpha,
			@ACParam(key="Comparator", defaultValue="$Comparator") ICComparator<X> comparator,
			@ACParam(key="Random", defaultValue="$Random")ICRandom random
	) {
		fKidAndEpsilonSet = new KidAndEpsilonSet(solutionTemplate, mu);
		fMu = mu;
		fSigma = Math.sqrt(variance);
		fAlpha = initAlpha;
		fCa = ca;
		fMuA = muAlpha;
		fDescentVector = new TCMatrix(dim, 1);
		fMeanOfParents = new TCMatrix(dim, 1);
		fTmp = new TCMatrix(dim, 1);
		fComparator = comparator;
		fRandom = random;
		fKidsSetComparator = new Comparator<KidAndEpsilon>() {
			@Override
			public int compare(KidAndEpsilon a, KidAndEpsilon b) {
				return fComparator.compare(a.fKid, b.fKid);
			}
		};
	}

	/**
	 * 
	 * なお，[秋本 09]によると，
	 * AREX+JGGにおける生成子個体数の推奨値は次元 <i>n</i> に対して <tt>noOfKids</tt> = <i>4n</i> である．
	 */
	public void makeOffspring(TCSolutionSet<X> parents, int noOfkids, TCSolutionSet<X> kids) {
		assert parents.size() == fMu;
		calcDecentDirectionAndMean(parents);
		fKidAndEpsilonSet.clear();
		fKidAndEpsilonSet.resize(noOfkids);
		generateKids(parents);
		kids.clear();
		for(KidAndEpsilon ke : fKidAndEpsilonSet) {
			kids.add(ke.fKid);
		}
	}

	/**
	 * 拡張率適応を実行する．
	 * doItを呼んで，kidsを生成して，kidsを評価した後に呼び出すこと．
	 */
	public void updateExpansionRate() {
		Collections.sort(fKidAndEpsilonSet, fKidsSetComparator);
		calcExpansionRate();				
	}

	public int getNoOfParents() {
		return fMu;
	}

	/**
	 * 拡張率を計算
	 */
	private void calcExpansionRate() {
		if(fKidAndEpsilonSet.size() == 0) return;
		calcLavg();
		calcLcdp();
		fAlpha *= Math.sqrt((1.0 - fCa) + fCa * fLcdp / fLavg);
		fAlpha = Math.max(fAlpha, 1.0);
	}
	
	/**
	 * 内部パラメータLavgを計算
	 */
	private void calcLavg() {
		fLavg = fAlpha * fSigma * (fMu - 1.0);
		fLavg = fLavg * fLavg / (double)fMuA;
	}

	/**
	 * 内部パラメータLcdpを計算
	 */
	private void calcLcdp(){
		fLcdp = fAlpha * fAlpha * (fMu - 1.0);
		double e1 = 0.0;
		double e2 = 0.0;
		for(int j=0; j<fMu; j++) {
			double ej = 0.0;
			for(int i=0; i<fMuA; i++) {
				ej += fKidAndEpsilonSet.get(i).fEpsilon[j];
			}
			ej /= (double)fMuA;
			e1 += ej*ej;
			e2 += ej;
		}
		fLcdp *= (e1 - e2 * e2 / (double)fMu);
	}
	
	/**
	 * 中心降下ベクトルおよび親個体の中心ベクトルを計算
	 * @param parents
	 */
	private void calcDecentDirectionAndMean(TCSolutionSet<X> parents) {
		assert parents.size() > 0;
		Collections.sort(parents, fComparator);
		fDescentVector.fill(0.0);
		fMeanOfParents.fill(0.0);
		
		double w = 0.0;
		for(int j=0; j<fMu; j++) {
			w = 2.0 * (fMu + 1.0 - (j + 1.0)) / (double)(fMu * (fMu + 1.0));
			fTmp.copyFrom(parents.get(j).getVector());
			fMeanOfParents.add(fTmp);
			fTmp.times(w);
			fDescentVector.add(fTmp);
		}
		fMeanOfParents.times(1.0 / (double)fMu);
	}
	
	/**
	 * 子個体の生成とその評価
	 * @param parents 親集団
	 */
	private void generateKids(TCSolutionSet<X> parents) {
		for(KidAndEpsilon ke : fKidAndEpsilonSet) {
			TCMatrix x = ke.fKid.getVector();
			x.copyFrom(fDescentVector);
			for(int j=0; j<fMu; j++) {
				fTmp.copyFrom(parents.get(j).getVector());
				fTmp.sub(fMeanOfParents);
				double ep = fRandom.nextGaussian(0.0, fSigma);
				fTmp.times(ep * fAlpha);
				ke.fEpsilon[j] = ep;
				x.add(fTmp);
			}
		}
	}
	
}
