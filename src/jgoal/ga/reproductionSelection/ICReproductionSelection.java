package jgoal.ga.reproductionSelection;

import java.io.Serializable;

import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;

/**
 * GAの複製選択器．
 * 復元抽出，および非復元抽出の両方で用いる．
 * 
 * @author uemura
 *
 * @param <X>
 */
public interface ICReproductionSelection<
	X extends ICSolution
> extends Serializable {

	/**
	 * 親個体の選択を行う．
	 * 復元抽出の場合は，population内の個体のコピーをparentsに登録する．
	 * 非復元抽出の場合は，populationから削除した個体をparentsに登録する．
	 * {@code parents} に抽出された親集団が格納される．
	 * {@code parents} は始めに{@code clear()} されること．
	 * 
	 * @param population 集団
	 * @param noOfParents 必要親個体数
	 * @param parents 親個体の集合が格納される個体集合．必要なサイズに予めリサイズしておくこと．
	 */
	public void doIt(TCSolutionSet<X> population, int noOfParents, TCSolutionSet<X> parents);

}
