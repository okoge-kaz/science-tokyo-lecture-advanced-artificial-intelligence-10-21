package jgoal.solution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import jssf.di.ACParam;

/**
 * A set of solutions.
 *
 * @param <X> the type of solutions to be contained
 * @since 2
 * @author isao
 */
public class TCSolutionSet<
	X extends ICSolution
> extends ArrayList<X> implements Cloneable, Serializable {

	/** For serialization */
	private static final long serialVersionUID = 1L;

	/** A solution factory */
	private TCSolutionFactory<X> fFactory = null;

	/**
	 * Creates an empty, unresizable solution set.
	 * Be careful to use the objects from this constructor; they do not have a solution factory
	 * and we have to set it before a call to {@link #resize(int)}.
	 *
	 * @since 2 isao
	 */
	public TCSolutionSet() {
	}

	/**
	 * Creates an empty, resizable solution set.
	 *
	 * @param factory a prototype of solutions
	 * @since 2 isao
	 */
	public TCSolutionSet(
			@ACParam(key = "SolutionTemplate") X solutionTemplate
	) {
		fFactory = new TCSolutionFactory<X>(solutionTemplate);
	}

	/**
	 * Copy constructor
	 * @param src
	 */
	public TCSolutionSet(TCSolutionSet<X> src) {
		fFactory = src.fFactory;
		addAllCopyOf(src);
	}
	
	/**
	 * クローン操作
	 */
	public TCSolutionSet<X> clone() {
		return new TCSolutionSet<X>(this);
	}

	/**
	 * Deep-copies the specified solution set to this object.
	 * Each content of the argument is deep-copied.
	 *
	 * @param src the source of the copy
	 * @return this object
	 * @since 2 isao
	 */
	public TCSolutionSet<X> copyFrom(TCSolutionSet<X> src) {
		fFactory = src.fFactory;
		clear();
		for (X s : src) {
			add(fFactory.createClone(s));
		}
		return this;
	}

	/**
	 * Changes the size of this solution set.
	 * When the size gets shrinked, the exceeding number of solutions are truncated from the tail of the set.
	 * Contrarily, if the size gets increased, the new initialized solutions are attached to the tail.
	 * It is guaranteed that the solutions placed in front of them are invariant during this operation.
	 *
	 * @param size the new size
	 * @since 2 isao
	 */
	public void resize(int size) {
		assert size >= 0;
		int diff = size - size();
		if (diff == 0) {
			return;
		}
		if (diff > 0) {	// the new size is bigger than the old
			do {
				add(fFactory.create());
				diff--;
			} while (diff > 0);
		} else {		// the new size is smaller than the old
			do {
				remove(size() - 1);
				diff++;
			} while (diff < 0);
		}
		assert size() == size;
	}

	public boolean addCopyOf(X src) {
		return add(fFactory.createClone(src));
	}

	public boolean addAllCopyOf(Collection<? extends X> src) {
		for (X s : src) {
			addCopyOf(s);
		}
		return src.size() != 0;
	}

	@SuppressWarnings("unchecked")
	public X getCopyOf(int i) {
		return (X)get(i).clone();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Size: " + size() + "\n");
		for (X s: this) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * 解ファクトリを返す．
	 * @return 解ファクトリ
	 */
	public TCSolutionFactory<X> getSolutionFactory() {
		return fFactory;
	}

}
