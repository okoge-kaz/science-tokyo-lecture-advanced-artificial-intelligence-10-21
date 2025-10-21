package jgoal.ga.survivalSelection;

import java.util.Collections;
import jgoal.solution.ICSolution;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.comparator.ICComparator;
import jssf.di.ACParam;
import jssf.random.ICRandom;
import jssf.util.TCRoulette;

/**
 * A survival selection that chooses two individuals from the family (an union of the parents and offspring)
 * and then replaces the first two of the parents with the chosen individuals.
 * The choice of the two is done by different manners for each.
 * The first individual is chosen by elitism and the second is done by the rank-based roulette wheel selection 
 * from the rest.
 *
 * @param <X> the type of individuals involved in this selection
 * @since 2
 * @author hmkz, isao
 */
public class TCBestAndRankBasedRouletteSelectionFromFamily<
	X extends ICSolution
> implements ICSurvivalSelection<X> {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	/** The candidate set of all the parents and offspring */
	private TCSolutionSet<X> fFamily;

	/** The roulette */
	private TCRoulette fRoulette;

	/** The comparator */
	private ICComparator<X> fComparator;

	/** The solutions selected for the next generation */
	private TCSolutionSet<X> fSelectedSolutions;

	/**
	 * Creates a survival selection.
	 * @param c comparator
	 * @param factory factory
	 * @param random random
	 * @since 2 isao
	 */
	public TCBestAndRankBasedRouletteSelectionFromFamily(
			@ACParam(key = "Comparator") ICComparator<X> c,
			@ACParam(key = "Random", defaultValue = "$Random") ICRandom random
	) {
		fComparator = c;
		fRoulette = new TCRoulette(random);
		fFamily = null;
		fSelectedSolutions = null;
	}

	@Override
	public void doIt(TCSolutionSet<X> population, TCSolutionSet<X> parents, TCSolutionSet<X> kids) {
		if (fFamily == null) {
			fFamily = new TCSolutionSet<X>(population.get(0));
			fSelectedSolutions = new TCSolutionSet<X>(population.get(0));
			fSelectedSolutions.resize(2);
		}
		registerToFamily(parents, kids);
		Collections.sort(fFamily, fComparator);
		X best = fFamily.get(0);
		X selected = chooseIndividualByRouletteWheelSelection(fFamily);
		assert best.getStatus() == ICSolution.Status.FEASIBLE;
		assert selected.getStatus() == ICSolution.Status.FEASIBLE;
		fSelectedSolutions.get(0).copyFrom(best);
		fSelectedSolutions.get(1).copyFrom(selected);
		population.add(best);
		population.add(selected);
		for (int i = 2; i < parents.size(); ++i) { //The first and second parents are eliminated.
			population.add(parents.get(i));
		}
	}

	/**
	 * Registers the specified parents and offspring to the family.
	 *
	 * @param parents a set of parents
	 * @param kids a set of offspring
	 * @since 2 hmkz, isao
	 */
	private void registerToFamily(TCSolutionSet<X> parents, TCSolutionSet<X> kids) {
		fFamily.clear();
		fFamily.add(parents.get(0));
		fFamily.add(parents.get(1));
		fFamily.addAll(kids);
		fRoulette.setNoOfSlots(fFamily.size());
	}

	/**
	 * Returns the selected individual by the ranked-roulette in the family without the best.
	 *
	 * @param family a family
	 * @return the selected individual
	 * @since 2 hmkz
	 */
	private X chooseIndividualByRouletteWheelSelection(TCSolutionSet<X> family) {
		fRoulette.resetCurrentSlotIndex();
		int noOfFeasibles = getNoOfFeasibleIndividuals(family);
		for(int i = 1; i < noOfFeasibles; i++) {
			fRoulette.setValueToSlot((double) (noOfFeasibles + 1 - i));
		}
		int selected = fRoulette.doIt() + 1;
		return family.get(selected);
	}

	/**
	 * Returns the number of the survivors in the specified family.
	 *
	 * @param family a family
	 * @return the number of the survivors in the family
	 * @since 2 isao
	 */
	private int getNoOfFeasibleIndividuals(TCSolutionSet<X> family) {
		int noOfFeasibles = 0;
		for (X ind: family) {
			if (ind.getStatus() != ICSolution.Status.FEASIBLE) {
				break;
			} else {
				++noOfFeasibles;
			}
		}
		return noOfFeasibles;
	}
	
	/*
	 * (non-Javadoc)
	 * @see jgoal.ga.survivalSelection.ICSurvivalSelection#getSelectedSolutions()
	 */
	@Override
	public TCSolutionSet<X> getSelectedSolutions() {
		return fSelectedSolutions;
	}

}
