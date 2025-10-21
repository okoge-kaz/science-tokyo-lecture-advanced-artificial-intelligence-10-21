package jgoal.solution;

import jssf.math.TCMatrix;

/**
 * Supports the real vector representation of objects. Real vectors are used as solutions
 * for function optimization problems or individuals for continuous reproduction operators
 * in evolutionary algorithms.
 *
 * @since 2
 * @author isao
 */
public interface ICRealCoding {

	/**
	 * Return the real vector representation of this object.
	 * If you modify the returned value of this method, call {@link #notifyVectorUpdated()}
	 * to update all the other representations of this object.
	 *
	 * @return the real vector representation of this object
	 * @since 2 isao
	 */
	TCMatrix getVector();

	/**
	 * ベクトルが更新されたことを通知する．
	 * getVectorメソッドで取得したベクトルの内容を更新した場合は，必ずこのメソッドを呼ぶこと．
	 *
	 * @since 2 isao
	 */
	void notifyVectorUpdated();

}
