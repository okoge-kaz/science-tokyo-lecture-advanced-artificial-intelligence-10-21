package jgoal.solution.comparator;

import java.io.Serializable;
import java.util.Comparator;

import jgoal.solution.ICSolution;

/**
 * A comparison function.
 * <p>
 * Implementors of comparators for solutions should implement this interface.
 * 
 * @param <X> the type of objects to be compared
 * @since 2
 * @author hmkz
 */
public interface ICComparator<X extends ICSolution> extends Comparator<X>, Serializable {
}
