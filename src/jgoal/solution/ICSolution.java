package jgoal.solution;

import java.io.Serializable;

/**
 * Represents the common features of solutions as the root interface.
 * Solutions express all possible answers for the problem to be solved, having either valid or invalid decision variables.
 * They support several fundamental operations, that is, to look up/be assigned feasibility, input/output with streams
 * and copy themselves.
 *
 * @since 2
 * @author isao
 */
public interface ICSolution extends Cloneable, Serializable {

	/**
	 * Represents the status of a solution.
	 *
	 * @since 2
	 * @author hmkz
	 */
	public enum Status {
		/** The flag that indicates this solution is feasible for the problem to be solved */
		FEASIBLE,

		/** The flag that indicates this solution is infeasible for the problem to be solved */
		INFEASIBLE,

		/** The flag that indicates this solution has been not evaluated yet. Should be used as an initial value for the status */
		NOT_EVALUATED,
	}
	
	/**
	 * クローンを作成する．
	 * @return クローン
	 */
	ICSolution clone();
	
	/**
	 * コピー操作
	 * @param src コピー元
	 * @return コピー元
	 */
	ICSolution copyFrom(ICSolution src);

	/**
	 * Returns the status of this object.
	 *
	 * @return Status.FEASIBLE, Status.INFEASIBLE or Status.NOT_EVALUATED
	 * when this object is feasible, infeasible or not evaluated
	 * @since 2 isao
	 */
	Status getStatus();

	/**
	 * Sets the status of this object.
	 *
	 * @param status the status
	 * @since 2 isao
	 */
	void setStatus(Status status);

}
