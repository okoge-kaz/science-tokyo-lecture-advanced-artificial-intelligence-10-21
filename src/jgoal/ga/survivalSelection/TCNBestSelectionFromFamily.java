package jgoal.ga.survivalSelection;

import java.util.Collections;

import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.comparator.ICComparator;
import jssf.di.ACParam;

/**
 * 家族内上位N個体を集団に戻す生存選択器．
 * 
 * @author uemura
 *
 * @param <X>
 */
public class TCNBestSelectionFromFamily<
	X extends ICSolution
> implements ICSurvivalSelection<X> {

	/**  */
	private static final long serialVersionUID = 1L;
	
	/** 個体比較器 */
	private ICComparator<X> fComparator;
	
	/** 家族＝親個体群+子個体群 */
	private TCSolutionSet<X> fFamily;
	
	/** 生存選択により選択された個体群 */
	private TCSolutionSet<X> fSelectedSolutions;

	public TCNBestSelectionFromFamily(
			@ACParam(key="Comparator", defaultValue="$Comparator") ICComparator<X> comparator
	) {
		fComparator = comparator;
		fFamily = null;
		fSelectedSolutions = null;
	}
	
	@Override
	public void doIt(TCSolutionSet<X> population, TCSolutionSet<X> parents, TCSolutionSet<X> kids) {
		if(fFamily == null) {
			fFamily = new TCSolutionSet<X>(parents.get(0));
			fSelectedSolutions = new TCSolutionSet<X>(parents.get(0));
		}
		fSelectedSolutions.clear();
		fFamily.clear();
		fFamily.addAll(parents);
		fFamily.addAll(kids);
		Collections.sort(fFamily, fComparator);
		int noOfParents = parents.size();
		for(int i=0; i<noOfParents; i++) {
			population.add(fFamily.get(i));
			fSelectedSolutions.add(fFamily.get(i));
		}
	}

	@Override
	public TCSolutionSet<X> getSelectedSolutions() {
		return fSelectedSolutions;
	}
}
