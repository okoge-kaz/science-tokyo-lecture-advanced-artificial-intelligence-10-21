package samples;

import jgoal.ga.TSUndxMgg;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.ICSolution.Status;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * UNDX+MGGを１試行実行するためのプログラム．設定は以下の通り：
 * ベンチマーク関数：k-tablet (k=n/4)，
 * 次元数：n=20，
 * 初期化領域：[-5,+5]^n，
 * 集団サイズ：12n，
 * 子個体生成数：6n，
 * 打ち切り評価回数：n × 1e5，
 * 打ち切り評価値：1.0 × 1e-7．
 * 
 * @author isao
 *
 */
public class TSUndxMggS {
	
	/**
	 * 初期集団の初期化を行う．
	 * @param population 初期集団
	 * @param min 初期化領域の最小値
	 * @param max 初期化領域の最大値
	 * @param random 乱数生成器
	 */
	private static void initializePopulation(TCSolutionSet<TSRealSolution> population, double min, double max, ICRandom random) {
		for (TSRealSolution s: population) {
			s.getVector().rand(random).times(max - min).add(min); //個体の座標を範囲[min, max]^nの乱数で初期化．
		}
	}

	/**
	 * 集団中の全ての個体の評価を行う．
	 * @param population 集団
	 */
	private static void evaluate(TCSolutionSet<TSRealSolution> population) {
		for (TSRealSolution s: population) {
			double eval = ktablet(s.getVector()); //k-tablet関数の値を得る．
			s.setEvaluationValue(eval); //個体に評価値を設定．
			s.setStatus(Status.FEASIBLE); //個体の状態を「実行可能」に設定．
		}
	}
	
	/**
	 * k-tablet関数 (k=n/4)
	 * @param s 個体
	 */
	private static double ktablet(TCMatrix x) {
		int k = (int)((double)x.getDimension() /4.0); //k=n/4
		double result = 0.0; //評価値を初期化
		for (int i = 0; i < x.getDimension(); ++i) {
			double xi = x.getValue(i); //i番目の次元の要素
			if (i < k) {
				result += xi * xi;				
			} else {
				result += 10000.0 * xi * xi;				
			}
		}
		return result;
	}
	
	/**
	 * メインメソッド．
	 * @param args なし
	 */
	public static void main(String[] args) {
		boolean minimization = true;
		int dimension = 20; //次元数
		int populationSize = 12 * dimension; //集団サイズ
		int noOfKids = 6 * dimension; //子個体生成数
		double min = -5.0; //初期化領域の最小値
		double max = +5.0; //初期化領域の最大値
		long maxEvals = (long)(dimension * 1e5); //打ち切り評価回数
		ICRandom random = new TCJava48BitLcg(); //乱数発生器
		TSUndxMgg ga = new TSUndxMgg(minimization, dimension, populationSize, noOfKids, random); //UNDX+MGG
		
		TCSolutionSet<TSRealSolution> population = ga.initialize(); //初期集団を生成
		initializePopulation(population, min, max, random); //初期集団を初期化
		evaluate(population); //初期集団を評価
		
		int noOfEvals = 0; //評価回数
		double best = ga.getBestEvaluationValue(); //集団中の最良評価値を得る．
		System.out.println(noOfEvals + " " + best); //集団中の最良評価値を画面に出力．
		while (best > 1e-7 && noOfEvals < maxEvals) { //終了条件．最良値が打ち切り評価値以下になったとき，もしくは，評価回数が，打ち切り評価回数を超えたとき．
			TCSolutionSet<TSRealSolution> offspring = ga.makeOffspring(); //子個体集合を生成．
			evaluate(offspring); //子個体集合を評価
			noOfEvals += offspring.size(); //評価回数を更新
			ga.nextGeneration(); //次世代に進める．
			best = ga.getBestEvaluationValue(); //集団中の最良評価値を得る．
			System.out.println(noOfEvals + " " + best); //集団中の最良評価値を画面に出力． 
		}
		
	}

}
