package jgoal.ga.reproduction;

import java.io.Serializable;

import jgoal.solution.ICRealSolution;
import jgoal.solution.TCSolutionSet;
import jssf.di.ACParam;
import jssf.math.TCMatrix;
import jssf.math.TCMatrixUtility;
import jssf.random.ICRandom;

/**
 * The Unimodal Normal Distribution Crossover (UNDX).
 *
 * @since 2
 * @author hmkz, uemura, isao
 */
public class TCUndx<X extends ICRealSolution> implements Serializable {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	/** A random number generator */
	private ICRandom fRandom;

	/** The parameter alpha that controls the expansion rate of the primary search component */
	private double fAlpha;

	/** The parameter beta that controls the expansion rate of the secondary search component */
	private double fBeta;

	/** The variance according to the primary search component */
	private double fSigma1;

	/** The variance according to the secondary search component */
	private double fSigma2;

	/** A unit vector directed from the first parent to the second one */
	private TCMatrix fEVector;

	/** The mean vector between the first and second parents */
	private TCMatrix fMean;

	/** True if the first and second parents are the (approximately) same */
	private boolean isParent1EqualToParent2;

	/**
	 * Creates an UNDX with the default parameters; &alpha;=0.5, &beta;=0.35.
	 *
	 * @since 2 hmkz
	 */
	public TCUndx(
		@ACParam(key="Random") ICRandom random
	) {
		this(0.5, 0.35, random); //alpha=0.5 and beta=0.35 are the recommended values, respectively.
	}

	/**
	 * Creates an UNDX with the specified parameters.
	 *
	 * @param alpha the expansion rate of the primary search component
	 * @param beta the expansion rate of the secondary search component
	 * @param random the random object
	 * @since 2 hmkz
	 */
	public TCUndx(
		@ACParam(key = "Alpha") double alpha,
		@ACParam(key = "Beta") double beta,
		@ACParam(key="Random") ICRandom random
	) {
		fAlpha = alpha;
		fBeta = beta;
		fRandom = random;
		fEVector = new TCMatrix();
		fMean = new TCMatrix();
	}

	/**
	 * Sets the parameter <i>alpha</i>, the expansion rate of the primary search component.
	 *
	 * @param alpha the expansion rate of the primary search component
	 * @since 2 hmkz
	 */
	public void setAlpha(double alpha) {
		fAlpha = alpha;
	}

	/**
	 * Returns the parameter <i>alpha</i>, the expansion rate of the primary search component.
	 *
	 * @return the parameter <i>alpha</i>
	 * @since 2 hmkz
	 */
	public double getAlpha() {
		return fAlpha;
	}

	/**
	 * Sets the parameter <i>beta</i>, the expansion rate of the secondary search component.
	 *
	 * @param beta the expansion rate of the secondary search component
	 * @since 2 hmkz
	 */
	public void setBeta(double beta) {
		fBeta = beta;
	}

	/**
	 * Returns the parameter <i>beta</i>, the expansion rate of the secondary search component.
	 *
	 * @return the parameter <i>beta</i>
	 * @since 2 hmkz
	 */
	public double getBeta() {
		return fBeta;
	}

	/**
	 * Generates offspring from the specified parents.
	 *
	 * @param parents a set of parents to generate offspring
	 * @param noOfKids a number of kids
	 * @param kids a buffer for offspring to be generated
	 * @since 2 hmkz
	 */
	public void makeOffspring(TCSolutionSet<X> parents, int noOfKids, TCSolutionSet<X> kids) {
		assert parents.size() == 3 : "The number of parents shound be three.";
		TCMatrix p1 = parents.get(0).getVector();
		TCMatrix p2 = parents.get(1).getVector();
		TCMatrix p3 = parents.get(2).getVector();
		int dimension = p1.getDimension();
		assert dimension == p2.getRowDimension() && dimension == p3.getRowDimension() && p2.getRowDimension() == p3.getRowDimension() : "The dimensions of parents should be the same.";

		calcMean(p1, p2);
		calcUnitVectorAndStandardDeviationForPrimaryComponent(p1, p2);
		calcUnitVectorAndStandardDeviationForSecondaryComponent(p1, p3);

		kids.resize(noOfKids);
		for (X kid : kids) {
			TCMatrix k = kid.getVector();
			k.setDimension(dimension);
			if (isParent1EqualToParent2) {
				k.copyFrom(p1);
				break;
			}
			// step.1 creates a random vector t
			TCMatrix t = new TCMatrix(dimension);
			for (int i = 0; i < dimension; i++) {
				t.setValue(i, 0, fRandom.nextGaussian(0.0, fSigma2));
			}
			// step.2 t <- t - (t . e)e
			TCMatrix tmpVector = new TCMatrix(fEVector);
			tmpVector.times(TCMatrixUtility.innerProduct(t, fEVector));
			t.sub(tmpVector);
			// step.3 t <- t + se
			tmpVector.copyFrom(fEVector);
			tmpVector.times(fRandom.nextGaussian(0.0, fSigma1));
			t.add(tmpVector);
			// step.4
			k.copyFrom(fMean);
			k.add(t);
		}
	}

	/**
	 * Returns the number of necessary parents.
	 * @return the number of parents
	 */
	public int getNoOfParents() {
		return 3;
	}

	/**
	 * Calculates the middle point between v1 and v2.
	 *
	 * @param v1 the first parent
	 * @param v2 the second parent
	 * @since 2 hmkz
	 */
	private void calcMean(TCMatrix v1, TCMatrix v2) {
		fMean.copyFrom(v1);
		fMean.add(v2);
		fMean.times(0.5);
	}

	/**
	 * Calculates the normalized vector of the primary search component which is a line from the parent 1 to 2
	 * and the standard deviation of them.
	 *
	 * @param v1 the first parent
	 * @param v2 the second parent
	 * @since 2 hmkz
	 */
	private void calcUnitVectorAndStandardDeviationForPrimaryComponent(TCMatrix v1, TCMatrix v2) {
		fEVector.copyFrom(v2);
		fEVector.sub(v1);
		double d1 = fEVector.normF(); // the distance from the first parent to the second one
		fSigma1 = fAlpha * d1; // the standard deviation of two parents on the primary component
//		if (TCComparator.equals(d1, 0.0))
		if (isEquals(d1, 0.0))
			isParent1EqualToParent2 = true;
		else
			isParent1EqualToParent2 = false;
		fEVector.times(1.0 / d1); // the normalized vector of the primary search component
	}

	/**
	 * Calculates the normalized vector of the secondary search component which is a line from
	 * the primary component to the third parent and the standard deviation of them.
	 *
	 * @param v1 the first parent
	 * @param v3 the third parent
	 * @since 2 hmkz
	 */
	private void calcUnitVectorAndStandardDeviationForSecondaryComponent(TCMatrix v1, TCMatrix v3) {
		TCMatrix v1v3 = new TCMatrix(v3); // the vector from v1 to v3
		v1v3.sub(v1);
		TCMatrix tmp = new TCMatrix(fEVector);
		tmp.times(TCMatrixUtility.innerProduct(fEVector, v1v3));
//		tmp.scalarProduct(fEVector.innerProduct(v1v3)); //TODO ‚Ç‚¤‚â‚Á‚Äs—ñ‰‰ŽZ‚Å“àÏ‚ð‘‚­‚©... uemura
		TCMatrix perpendicular = new TCMatrix(v1v3); // the perpendicular vector from v3 to the primary component
		perpendicular.sub(tmp);
		double d2 = perpendicular.normF();
		fSigma2 = fBeta * d2 / Math.sqrt((double)v1.getRowDimension()); // the normalized vector of the secondary component
	}
	
	/**
	 * Œë·‚ðl—¶‚µ‚Ä”äŠr
	 * 20110707 jgoal2010‚ÌTCComparator‚ÉŽÀ‘•‚³‚ê‚Ä‚¢‚é‚ªCˆÚA‚·‚é‚©•s–¾‚Ì‚½‚ß‚±‚±‚Éì¬D
	 * @author uemura
	 * @param d1
	 * @param d2
	 * @return
	 */
	private boolean isEquals(double d1, double d2) {
		return Math.abs(d1 - d2) <= 1e-15;
	}
	
	
}
