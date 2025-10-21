package jgoal.solution.comparator;

import jgoal.solution.ICSolution.Status;
import jgoal.solution.ISSolution;
import jssf.di.ACParam;

/**
 * Compares solutions based on the objective function of the single-objective problem.
 * <h3>Properties:</h3>
 * No parameter required.
 *
 * @since 2
 * @author isao
 */
public class TSEvaluationValueComparator<X extends ISSolution> implements ISComparator<X> {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	/** The flag that indicates the problem requires minimization of the objective function */
	private boolean fIsMinimization;

	/**
	 * Creates an evaluation value comparator.
	 *
	 * @param minimization
	 * @since 2 isao
	 */
	public TSEvaluationValueComparator(
		@ACParam(key = "Minimization") boolean minimization
	) {
		fIsMinimization = minimization;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * @since 2 hmkz, isao
	 */
	public int compare(ISSolution a, ISSolution b) {
		if ((a.getStatus() == Status.FEASIBLE && b.getStatus() == Status.FEASIBLE) 
				|| (a.getStatus() == Status.INFEASIBLE && b.getStatus() == Status.INFEASIBLE)) {
			int sgn = 0;
			if (a.getEvaluationValue() - b.getEvaluationValue() < 0) {
				sgn = -1;
			} else if (a.getEvaluationValue() - b.getEvaluationValue() > 0) {
				sgn = 1;
			}
			return fIsMinimization ? sgn: -sgn;
		} else if (a.getStatus() == Status.FEASIBLE && b.getStatus() == Status.INFEASIBLE) {
			return -1;
		} else if (a.getStatus() == Status.INFEASIBLE && b.getStatus() == Status.FEASIBLE) {
			return 1;
		} else {
			throw new RuntimeException("The solutions have not been evaluated!!");
		}
	}

}
