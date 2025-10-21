package jgoal.solution;

import java.util.Collections;

import jgoal.solution.ICSolution.Status;
import jgoal.solution.comparator.TSEvaluationValueComparator;

public class TTestEvaluationValueComparator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TCSolutionSet<TSRealSolution> pop = new TCSolutionSet<TSRealSolution>(new TSRealSolution());
		pop.resize(5);
		pop.get(0).setStatus(Status.INFEASIBLE);
		pop.get(0).setEvaluationValue(3.0);

		pop.get(1).setStatus(Status.FEASIBLE);
		pop.get(1).setEvaluationValue(10.0);

		pop.get(2).setStatus(Status.INFEASIBLE);
		pop.get(2).setEvaluationValue(2.0);
		
		pop.get(3).setStatus(Status.FEASIBLE);
		pop.get(3).setEvaluationValue(5.0);

		pop.get(4).setStatus(Status.FEASIBLE);
		pop.get(4).setEvaluationValue(1.0);
		
		TSEvaluationValueComparator<TSRealSolution> cmp = new TSEvaluationValueComparator<TSRealSolution>(true);
		Collections.sort(pop, cmp);
		
		System.out.println(pop);
		
	}

}
