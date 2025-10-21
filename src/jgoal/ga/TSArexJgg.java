package jgoal.ga;

import java.io.Serializable;
import java.util.Collections;
import jgoal.ga.reproduction.TCArex;
import jgoal.ga.reproductionSelection.ICReproductionSelection;
import jgoal.ga.reproductionSelection.TCRandomSelectionWithoutReplacement;
import jgoal.ga.survivalSelection.ICSurvivalSelection;
import jgoal.ga.survivalSelection.TCNBestSelectionFromKids;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.comparator.ICComparator;
import jgoal.solution.comparator.TSEvaluationValueComparator;
import jssf.di.ACParam;
import jssf.random.ICRandom;

/**
 * AREX/JGG
 * 
 * @author isao
 * @param <X>
 */
public class TSArexJgg implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 次元数 */
	private int fDimension;
	
	/** 集団 */
	private TCSolutionSet<TSRealSolution> fPopulation;
	
	/** 集団サイズ */
	private int fPopulationSize;
	
	/** 親個体集合 */
	private TCSolutionSet<TSRealSolution> fParents;
	
	/** 子個体集合 */
	private TCSolutionSet<TSRealSolution> fKids;
	
	/** 生成子個体数 */
	private int fNoOfKids;

	/** 複製選択器 */
	private ICReproductionSelection<TSRealSolution> fReproductionSelection;

	/** 子個体生成器（交叉） */
	private TCArex<TSRealSolution> fArex;
	
	/** 生存選択器 */
	private ICSurvivalSelection<TSRealSolution> fSurvivalSelection;
	
	/** 乱数発生器 */
	private ICRandom fRandom;
	
	/** 個体比較器 */
	private ICComparator<TSRealSolution> fComparator;
	
	/** 個体テンプレート */
	private TSRealSolution fSolutionTemplate;
	
	/**
	 * コンストラクタ．
	 * @param solution 解のテンプレート
	 * @param noOfParents 親個体数
	 * @param noOfKids 生成子個体数
	 * @param reproductionSelection 複製選択器
	 * @param reproduction 子個体生成器（交叉）
	 * @param survivalSelection 生存選択器
	 */
	public TSArexJgg(
			@ACParam(key="Minimization") boolean minimization,
			@ACParam(key="Dimension") int dimension,
			@ACParam(key="PopulationSize") int populationSize,
			@ACParam(key="NoOfKids") int noOfKids,
			@ACParam(key="random") ICRandom random
	) {
		fDimension = dimension;
		fPopulationSize = populationSize;
		fRandom = random;
		fNoOfKids = noOfKids;
		fSolutionTemplate = new TSRealSolution(fDimension);
		fComparator = new TSEvaluationValueComparator<TSRealSolution>(minimization);
	}

	/**
	 * 初期化する．
	 * @return 初期集団
	 */
	public TCSolutionSet<TSRealSolution> initialize() {
		fParents = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fKids = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fReproductionSelection = new TCRandomSelectionWithoutReplacement<TSRealSolution>(fRandom);
		fArex = new TCArex<TSRealSolution>(fSolutionTemplate, fDimension, fComparator, fRandom);
		fSurvivalSelection = new TCNBestSelectionFromKids<TSRealSolution>(fComparator);
		fPopulation = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fPopulation.resize(fPopulationSize);
		return fPopulation;
	}
	
	/**
	 * 集団を返す．
	 * @return 集団
	 */
	public TCSolutionSet<TSRealSolution> getPopulation() {
		return fPopulation;
	}
	
	/**
	 * 子個体集合を生成して返す．
	 * @return 子個体集合
	 */
	public TCSolutionSet<TSRealSolution> makeOffspring() {
		fParents.clear(); //親個体集合をクリアする．
		fKids.clear(); //子個体集合をクリアする．
		fReproductionSelection.doIt(fPopulation, fArex.getNoOfParents(), fParents); //生存選択を行う．
		fArex.makeOffspring(fParents, fNoOfKids, fKids); //AREXにより子個体集合を生成する．
		return fKids;
	}
	
	/**
	 * 世代を進める．
	 */
	public void nextGeneration() {
		fArex.updateExpansionRate(); //AREXの拡張率適応を実行する．
		fSurvivalSelection.doIt(fPopulation, fParents, fKids); //生存選択を行う．
	}
	
	/**
	 * 集団中の最良個体を返す．
	 * @return 集団中の最良個体
	 */
	public TSRealSolution getBestIndividual() {
		Collections.sort(fPopulation, fComparator);
		return fPopulation.get(0);
	}
	
	/**
	 * 集団中の最良個体の評価値を返す．
	 * @return 集団中の最良個体の評価値
	 */
	public double getBestEvaluationValue() {
		return getBestIndividual().getEvaluationValue();
	}
	
}
