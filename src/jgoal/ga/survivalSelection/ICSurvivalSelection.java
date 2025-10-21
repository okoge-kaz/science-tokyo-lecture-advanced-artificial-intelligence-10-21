package jgoal.ga.survivalSelection;

import java.io.Serializable;

import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;

/**
 * 生存選択器．
 * @author uemura
 *
 * @param <X>
 */
public interface ICSurvivalSelection<
	X extends ICSolution
> extends Serializable {

	/**
	 * 生存選択を行う．
	 * @param population 集団
	 * @param parents 親個体集合
	 * @param kids 生成子個体集合
	 */
	void doIt(TCSolutionSet<X> population, TCSolutionSet<X> parents, TCSolutionSet<X> kids);
	
	/**
	 * 生存選択により選ばれた個体群を返す．
	 * @return 生存選択により選ばれた個体群
	 */
	TCSolutionSet<X> getSelectedSolutions();

}
