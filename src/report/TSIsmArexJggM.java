package report;

import java.io.IOException;

import jgoal.ga.TSArexJgg;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.ICSolution.Status;
import jssf.log.TCTable;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * Performs ISM with AREX/JGG for 10 trials.
 * The transition of the best evaluation value in the population in each trial is stored in a log file.
 * The log file is saved in the CSV format.
 * The experimental setting is as follows.
 * - Benchmark function： the double sphere function，
 * - Dimension： n=10，
 * - Initial region: [-5,+5]^n，
 * - Population size： 14n，
 * - The number of offspring： 5n，
 * - The maximum number of evaluations for each iteration: 50000,
 * - The maximum number of iterations for ISM: 20,
 * - r for ISM: 0.1
 * - Log filename： IsmArexJggDoubleSphereUVr0_1.csv
 *
 * @author isao
 *
 */
public class TSIsmArexJggM {

	/**
	 * Initializes the population.
	 * @param population the population
	 * @param min the minimum of the initial region.
	 * @param max the maximum of the initial region.
	 * @param random the random generator.
	 */
	private static void initializePopulation(TCSolutionSet<TSRealSolution> population, double min, double max, double r, ICRandom random) {
		final int dimension = population.get(0).getVector().getDimension();
		TCMatrix center = new TCMatrix(dimension).rand(random).times(max - min).add(min);	//Calculates the center of the initial region.
		double d = r * (max - min) / 2.0;
		for (TSRealSolution s: population) {
			s.getVector().rand(random).times(2.0 * d).add(center).sub(d); //Initializes each variable with a uniformly random number between center - min and center + max.
		}
	}

	/**
	 * Evaluates the population.
	 * @param population population
	 */
	private static void evaluate(TCSolutionSet<TSRealSolution> population) {
		for (TSRealSolution s: population) {
			double eval = doubleSphereUV(s.getVector()); //Calculates the value of the double sphere function.
			s.setEvaluationValue(eval); //Sets the function value to the individual.
			s.setStatus(Status.FEASIBLE); //Sets "feasible" to the status of the individual.
		}
	}

	/**
	 * The double sphere function
	 * @param s solution
	 * @return the evaluation value of the solution
	 */
	private static double doubleSphereUV(TCMatrix x) {
		double eval1 = 0.0, eval2 = 0.0;
		for(int i = 0; i < x.getDimension(); i++) {
			eval1 += 2.0 * (x.getValue(i) + 2.0) * (x.getValue(i) + 2.0);
			eval2 += 1.0 * (x.getValue(i) - 2.0) * (x.getValue(i) - 2.0);
		}
		return Math.min(eval1, eval2 + 0.1);
	}

	/**
	 * Registers the number of evaluations and the best evaluation value to the log table.
	 * @param log the log table
	 * @param trialName the trial name, which used for a header label in the log table.
	 * @param trialNo the trial number, which used for a header label in the log table.
	 * @param index the index of line in the log table.
	 * @param noOfEvals the number of evaluations.
	 * @param bestEvaluationValue the best evaluation value.
	 */
	private static void putLogData(TCTable log, String trialName, int trialNo, int index, long noOfEvals, double bestEvaluationValue) {
		log.putData(index, "NoOfEvals", noOfEvals);
		log.putData(index, trialName + "_" + trialNo, bestEvaluationValue);
	}

	/**
	 * Performs one trial.
	 * @param ga AREX/JGG
	 * @param maxEvals The maximum number of evaluations for AREX/JGG
	 * @param maxMsIteration The maximum number of iterations for ISM
	 * @param min Minimum of the initial region
	 * @param max Maximum of the initial region
	 * @param r r of ISM
	 * @param random Random generator
	 * @param log Log table
	 * @param trialName Trial name, which is used as a header label in the log table.
	 * @param trialNo Trial number, which is used as a header label in the log table.
	 */

	private static boolean executeOneTrial(TSArexJgg ga, long maxEvals, int maxIsmIteration, double min, double max, double r, ICRandom random,
																			TCTable log, String trialName, int trialNo) {
		double stopEval = 1e-7; //The evaluation value where the loop stops.
		long noOfAllEvals = 0; //The total number of evaluations.
		int logIndex = 0; //Initializes the index of the log table.
		for(int ismIteration = 0; ismIteration < maxIsmIteration; ++ismIteration) { //The loop for ISM.
			TCSolutionSet<TSRealSolution> population = ga.initialize(); //Obtains a population.
			initializePopulation(population, min, max, r, random); //Initializes the population.
			evaluate(population); //Evaluates the population.
			long noOfEvals = 0; //Initializes the number of evaluations for AREX/JGG.
			double best = ga.getBestEvaluationValue(); //Gets the best evaluation value in the population.
			putLogData(log, trialName, trialNo, logIndex, noOfAllEvals, best); //Register the information of the population to the log.
			++logIndex; //Increments the index of the log table.
			int generationCounter = 0; //Initializes the generation counter for AREX/JGG.
			while (best > stopEval && noOfEvals < maxEvals) { //If the optimum is found or the number of evaluations exceeds the maximum, AREX/JGG stops.
				TCSolutionSet<TSRealSolution> offspring = ga.makeOffspring(); //Generates offspring.
				evaluate(offspring); //Evaluates the offspring.
				noOfEvals += offspring.size(); //Updates the number of evaluations.
				noOfAllEvals += offspring.size(); //Updates the total number of evaluations.
				ga.nextGeneration(); //Advances one generation.
				best = ga.getBestEvaluationValue(); //Obtains the best evaluation value in the population.
				if (generationCounter % 10 == 0) {
					putLogData(log, trialName, trialNo, logIndex, noOfAllEvals, best);
					++logIndex; //Increments the index of the log table.
				}
				++generationCounter;
			}
			putLogData(log, trialName, trialNo, logIndex, noOfAllEvals, best); //Registers the information of the final population to the log.
			System.out.println("TrialNo:" + trialNo + ", IsmIteration: " + ismIteration +  ", TotalNoOfEvals:" + noOfAllEvals + ", Best:" + best);
			if(best <= stopEval) {
				return true; //If the optimum has been found, return true;
			}
		}
		return false; //If the optimum has not been found, return false.
	}

	/**
	 * Main method
	 * @param args none
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		double r = 0.1;	// r of ISM. The width of the initial region is r(max-min).
		String trialName = "IsmArexJggDoubleSphereUVr0_1"; //Trial name
		double min = -5.00; //Minimum of the initial region
		double max = +5.00; //Maximum of the initial region
		boolean minimization = true; //Minimization
		int dimension = 10; //Dimension
		int populationSize = 14 * dimension; //Population size
		int noOfKids = 5 * dimension; //The number of offspring
		int maxIsmIterations = 20;	//The maximum number of iterations for ISM.
		long maxEvals = 50000L; //The maximum number of evaluations for AREX/JGG.
		int maxTrials = 10; //The number of trials.
		String logFilename = trialName + ".csv"; //Log filename
		ICRandom random = new TCJava48BitLcg(); //Random generator
		TSArexJgg ga = new TSArexJgg(minimization, dimension, populationSize, noOfKids, random); //AREX/JGG
		TCTable log = new TCTable(); //Log table.
		int noOfSuccessfulTrials = 0;
		for (int trial = 0; trial < maxTrials; ++trial) {
			boolean result = executeOneTrial(ga, maxEvals, maxIsmIterations, min, max, r, random, log, trialName, trial); //Performs one trial.
			if (result) {
				++noOfSuccessfulTrials;
			}
		}
		System.out.println("No. of successful trials: " + noOfSuccessfulTrials);
		log.writeTo(logFilename); // Outputs logs of ten trials to the log file.
	}

}
