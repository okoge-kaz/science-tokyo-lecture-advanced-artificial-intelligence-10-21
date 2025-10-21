package jgoal.solution;

/**
 * 単目的最適化のための個体インターフェース
 *
 * @since 2
 * @author isao
 */
public interface ISSolution extends ICSolution {

	/**
	 * 評価値を返す．
	 * @return 評価値
	 * @since 2 isao
	 */
	double getEvaluationValue();

	/**
	 * 評価値を設定する．
	 * @param value 評価値
	 * @since 2 isao
	 */
	void setEvaluationValue(double value);

}
