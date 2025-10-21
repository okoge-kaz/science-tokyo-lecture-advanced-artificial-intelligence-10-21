package jgoal.solution;

import jssf.di.ACParam;
import jssf.math.TCMatrix;;

/**
 * Represents an individual for single-objective real-coded GAs.
 *
 * @see TCMatrix
 * @since 2
 * @author hmkz
 */
public class TSRealSolution implements ISRealSolution {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	/** A status of this individual as a solution */
	private Status fStatus;

	/** An evaluation value of an objective function */
	private double fEvaluationValue;

	/** A real vector */
	private TCMatrix fRealVector;

	/**
	 * Creates an individual with a zero dimensional vector.
	 *
	 * @since 2 hmkz
	 */
	public TSRealSolution() {
		this(0);
	}

	/**
	 * Creates an individual with the specified-dimensional vector.
	 *
	 * @param dimension the number of elements of a vector
	 * @since 2 hmkz
	 */
	public TSRealSolution(
			@ACParam(key = "Dimension") int dimension
	) {
		fStatus = Status.NOT_EVALUATED;
		fEvaluationValue = Double.NaN;
		fRealVector = new TCMatrix(dimension, 1);
	}

	/**
	 * コピーコンストラクタ
	 * @param src コピー元
	 */
	public TSRealSolution(TSRealSolution src) {
		fStatus = src.fStatus;
		fEvaluationValue = src.fEvaluationValue;
		fRealVector = new TCMatrix(src.fRealVector);
	}
	
	/**
	 * クローン操作
	 */
	@Override
	public TSRealSolution clone() {
		return new TSRealSolution(this);
	}

	/*
	 * (non-Javadoc)
	 * @see jgoal.core.ICState#copyFrom(jgoal.core.ICState)
	 * @since 2 hmkz
	 */
	@Override
	public TSRealSolution copyFrom(ICSolution src) {
		TSRealSolution _src = (TSRealSolution) src;
		fStatus = _src.fStatus;
		fEvaluationValue = _src.fEvaluationValue;
		fRealVector.copyFrom(_src.fRealVector);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see jgoal.solution.ISSolution#getEvaluationValue()
	 * @since 2 hmkz
	 */
	@Override
	public double getEvaluationValue() {
		return fEvaluationValue;
	}

	/*
	 * (non-Javadoc)
	 * @see jgoal.solution.ISSolution#setEvaluationValue(double)
	 * @since 2 hmkz
	 */
	@Override
	public void setEvaluationValue(double value) {
		fEvaluationValue = value;
	}

	/*
	 * (non-Javadoc)
	 * @see jgoal.solution.ICSolution#getStatus()
	 * @since 2 hmkz
	 */
	@Override
	public Status getStatus() {
		return fStatus;
	}

	/*
	 * (non-Javadoc)
	 * @see jgoal.solution.ICSolution#setStatus(jgoal.solution.ICSolution.Status)
	 * @since 2 hmkz
	 */
	@Override
	public void setStatus(Status status) {
		fStatus = status;
	}

	/*
	 * (non-Javadoc)
	 * @see jgoal.solution.ICRealCoding#getVector()
	 * @since 2 hmkz
	 */
	@Override
	public TCMatrix getVector() {
		return fRealVector;
	}

	/*
	 * (non-Javadoc)
	 * @see jgoal.solution.ICRealCoding#notifyVectorUpdated()
	 * @since 2 hmkz
	 */
	@Override
	public void notifyVectorUpdated() {
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * @since 2 hmkz
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("status=").append(fStatus);
		sb.append(", f(x)=").append(fEvaluationValue);
		sb.append(", x=").append(fRealVector.clone().transpose());
		return sb.toString();
	}

}
