package jgoal.ga;

import java.io.Serializable;
import java.util.Collections;
import jgoal.ga.reproduction.TCUndx;
import jgoal.ga.reproductionSelection.ICReproductionSelection;
import jgoal.ga.reproductionSelection.TCRandomSelectionWithoutReplacement;
import jgoal.ga.survivalSelection.ICSurvivalSelection;
import jgoal.ga.survivalSelection.TCBestAndRankBasedRouletteSelectionFromFamily;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.comparator.ICComparator;
import jgoal.solution.comparator.TSEvaluationValueComparator;
import jssf.di.ACParam;
import jssf.random.ICRandom;

/**
 * UNDX+MGG
 * 
 * @author isao
 * @param <X>
 */
public class TSUndxMgg implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Dimension */
	private int fDimension;
	
	/** The population */
	private TCSolutionSet<TSRealSolution> fPopulation;
	
	/** The population size */
	private int fPopulationSize;
	
	/** The parent set */
	private TCSolutionSet<TSRealSolution> fParents;
	
	/** The offspring set */
	private TCSolutionSet<TSRealSolution> fKids;
	
	/** The number of offspring */
	private int fNoOfKids;

	/** The mating selection operator */
	private ICReproductionSelection<TSRealSolution> fReproductionSelection;

	/** UNDX */
	private TCUndx<TSRealSolution> fUndx;
	
	/** The survival selection operator */
	private ICSurvivalSelection<TSRealSolution> fSurvivalSelection;
	
	/** The random number generator */
	private ICRandom fRandom;
	
	/** The comparator of individuals */
	private ICComparator<TSRealSolution> fComparator;
	
	/** The solution template */
	private TSRealSolution fSolutionTemplate;
	
	/**
	 * Constructor
	 * @param solution 解のテンプレート
	 * @param noOfParents 親個体数
	 * @param noOfKids 生成子個体数
	 * @param reproductionSelection 複製選択器
	 * @param reproduction 子個体生成器（交叉）
	 * @param survivalSelection 生存選択器
	 */
	public TSUndxMgg(
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
		fUndx = new TCUndx<TSRealSolution>(fRandom);
		fSurvivalSelection = new TCBestAndRankBasedRouletteSelectionFromFamily<TSRealSolution>(fComparator, fRandom);
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
		fReproductionSelection.doIt(fPopulation, fUndx.getNoOfParents(), fParents); //生存選択を行う．
		fUndx.makeOffspring(fParents, fNoOfKids, fKids); //AREXにより子個体集合を生成する．
		return fKids;
	}
	
	/**
	 * 世代を進める．
	 */
	public void nextGeneration() {
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
