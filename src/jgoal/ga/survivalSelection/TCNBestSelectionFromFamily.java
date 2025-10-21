package jgoal.ga.survivalSelection;

import java.util.Collections;

import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.comparator.ICComparator;
import jssf.di.ACParam;

/**
 * ‰Æ‘°“àãˆÊNŒÂ‘Ì‚ğW’c‚É–ß‚·¶‘¶‘I‘ğŠíD
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
	
	/** ŒÂ‘Ì”äŠrŠí */
	private ICComparator<X> fComparator;
	
	/** ‰Æ‘°eŒÂ‘ÌŒQ+qŒÂ‘ÌŒQ */
	private TCSolutionSet<X> fFamily;
	
	/** ¶‘¶‘I‘ğ‚É‚æ‚è‘I‘ğ‚³‚ê‚½ŒÂ‘ÌŒQ */
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
