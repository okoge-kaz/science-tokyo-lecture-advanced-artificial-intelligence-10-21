package jssf.random;

import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * This abstract class provides the default implementation of random number generator's methods.
 * この抽象クラスを継承したクラスは，以下の２つのメソッドのみを実装すれば，全ての乱数メソッドが利用可能になる．
 * <ul>
 *  <li>乱数系列の種を設定するresetSeed(long seed)
 *  <li>bitsビットの一様乱数を生成するnext(int bits)
 * </ul>
 *
 * @since 2
 * @author hmkz
 */
public abstract class TCAbstractRandom implements ICRandom {

	private static final long serialVersionUID = 1L;

	/**
	 * Sets the specified value {@code seed} into the seed of the random number sequence of this object.
	 *
	 * @param seed a new seed for the random number sequence
	 * @since 2 hmkz
	 */
	protected abstract void resetSeed(long seed);

	/**
	 * Returns a random bit sequence of the specified bits packed into an {@code int} value,
	 * assuming the lower {@code bits} bits are uniformly-random (probabilities of being 0 or 1 are equal)
	 * and the upper {@code (32 - bits)} bits are zero.
	 *
	 * @param bits the number of bits to be generated. {@code bits} must be between 1 and 32.
	 * @return the random bit sequence of {@code bits} bits
	 * @since 2 hmkz
	 */
	protected abstract int next(int bits);

	@Override
	public final void setSeed(long seed) {
    	haveNextNextGaussian = false;
    	resetSeed(seed);
	}

	@Override
	public boolean nextBoolean() {
		return next(1) != 0;
	}

	@Override
	public boolean nextBoolean(double pTrue) {
		return nextDouble() < pTrue;
	}

	@Override
	public char nextChar() {
		return (char) next(16);
	}

	@Override
	public char nextChar(char n) {
        assert n > 0;

        if ((n & -n) == n)  // i.e., n is a power of 2
            return (char) (next(15) & (n-1));

        int bits, val;
        do {
            bits = next(15);
            val = bits % n;
        } while (bits - val + (n-1) < 0);	// discard if val is biased
        return (char) val;
	}

	@Override
	public char nextChar(char min, char max) {
		return (char) (nextChar((char) (max - min + 1)) + min);
	}

	@Override
	public byte nextByte() {
		return (byte) next(8);
	}

	@Override
	public byte nextByte(byte n) {
        assert n > 0;

        if ((n & -n) == n)  // i.e., n is a power of 2
            return (byte) (next(7) & (n-1));

        int bits, val;
        do {
            bits = next(7);
            val = bits % n;
        } while (bits - val + (n-1) < 0);	// discard if val is biased
        return (byte) val;
	}

	@Override
	public byte nextByte(byte min, byte max) {
		return (byte) (nextByte((byte) (max - min + 1)) + min);
	}

	@Override
	public short nextShort() {
		return (short) next(16);
	}

	@Override
	public short nextShort(short n) {
        assert n > 0;

        if ((n & -n) == n)  // i.e., n is a power of 2
            return (short) (next(15) & (n-1));

        int bits, val;
        do {
            bits = next(15);
            val = bits % n;
        } while (bits - val + (n-1) < 0);	// discard if val is biased
        return (short) val;
	}

	@Override
	public short nextShort(short min, short max) {
		return (short) (nextShort((short) (max - min + 1)) + min);
	}

	@Override
	public int nextInt() {
		return next(32);
	}

	@Override
	public int nextInt(int n) {
        assert n > 0;

        if ((n & -n) == n)  // i.e., n is a power of 2
            return next(31) & (n-1);

        int bits, val;
        do {
            bits = next(31);
            val = bits % n;
        } while (bits - val + (n-1) < 0);	// discard if val is biased
        return val;
	}

	@Override
	public int nextInt(int min, int max) {
		return nextInt(max - min + 1) + min;
	}

	@Override
	public long nextLong() {
        return ((long)(next(32)) << 32) + next(32);
	}

	@Override
	public long nextLong(long n) {
        assert n > 0;

        if ((n & -n) == n)  // i.e., n is a power of 2
            return nextLong() & (n-1);

        long bits, val;
        do {
            bits = nextLong() & Long.MAX_VALUE;	// assume bits is positive
            val = bits % n;
        } while (bits - val + (n-1) < 0);	// discard if val is biased
        return val;
	}

	@Override
	public long nextLong(long min, long max) {
		return nextLong(max - min + 1L) + min;
	}

	@Override
	public float nextFloat() {
        return next(24) / ((float)(1 << 24));
	}

	@Override
	public float nextFloat(float sup) {
		return nextFloat() * sup;
	}

	@Override
	public float nextFloat(float min, float max) {
		return nextFloat() * (max - min) + min;
	}

	@Override
	public double nextDouble() {
        return (((long)(next(26)) << 27) + next(27)) / (double)(1L << 53);
	}

	@Override
	public double nextDouble(double sup) {
		return nextDouble() * sup;
	}

	@Override
	public double nextDouble(double min, double max) {
		return nextDouble() * (max - min) + min;
	}

	@Override
	synchronized public double nextGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if (haveNextNextGaussian) {
    	    haveNextNextGaussian = false;
    	    return nextNextGaussian;
    	} else {
            double v1, v2, s;
    	    do {
                v1 = 2 * nextDouble() - 1; // between -1 and 1
            	v2 = 2 * nextDouble() - 1; // between -1 and 1
                s = v1 * v1 + v2 * v2;
    	    } while (s >= 1 || s == 0);
    	    double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
    	    nextNextGaussian = v2 * multiplier;
    	    haveNextNextGaussian = true;
    	    return v1 * multiplier;
        }
	}
    private double nextNextGaussian;
    private boolean haveNextNextGaussian = false;

	@Override
	public double nextGaussian(double mu, double sigma) {
		return nextGaussian() * sigma + mu;
	}

	@Override
	public void fill(boolean[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextBoolean();
		}
	}

	@Override
	public void fill(boolean[] buf, double pTrue) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextBoolean(pTrue);
		}
	}

	@Override
	public void fill(char[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextChar();
		}
	}

	@Override
	public void fill(char[] buf, char n) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextChar(n);
		}
	}

	@Override
	public void fill(char[] buf, char min, char max) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextChar(min, max);
		}
	}

	@Override
	public void fill(byte[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextByte();
		}
	}

	@Override
	public void fill(byte[] buf, byte n) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextByte(n);
		}
	}

	@Override
	public void fill(byte[] buf, byte min, byte max) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextByte(min, max);
		}
	}

	@Override
	public void fill(short[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextShort();
		}
	}

	@Override
	public void fill(short[] buf, short n) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextShort(n);
		}
	}

	@Override
	public void fill(short[] buf, short min, short max) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextShort(min, max);
		}
	}

	@Override
	public void fill(int[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextInt();
		}
	}

	@Override
	public void fill(int[] buf, int n) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextInt(n);
		}
	}

	@Override
	public void fill(int[] buf, int min, int max) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextInt(min, max);
		}
	}

	@Override
	public void fill(long[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextLong();
		}
	}

	@Override
	public void fill(long[] buf, long n) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextLong(n);
		}
	}

	@Override
	public void fill(long[] buf, long min, long max) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextLong(min, max);
		}
	}

	@Override
	public void fill(float[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextFloat();
		}
	}

	@Override
	public void fill(float[] buf, float sup) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextFloat(sup);
		}
	}

	@Override
	public void fill(float[] buf, float min, float max) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextFloat(min, max);
		}
	}

	@Override
	public void fill(double[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextDouble();
		}
	}

	@Override
	public void fill(double[] buf, double sup) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextDouble(sup);
		}
	}

	@Override
	public void fill(double[] buf, double min, double max) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = nextDouble(min, max);
		}
	}

	@Override
	public void shuffle(boolean[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(boolean[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(boolean[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			boolean tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(char[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(char[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(char[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			char tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(byte[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(byte[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(byte[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			byte tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(short[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(short[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(short[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			short tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(int[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(int[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(int[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			int tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(long[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(long[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(long[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			long tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(float[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(float[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(float[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			float tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(double[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(double[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(double[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			double tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(Object[] array) {
		shuffle(array, array.length);
	}

	@Override
	public void shuffle(Object[] array, int n) {
		shuffle(array, 0, n - 1);
	}

	@Override
	public void shuffle(Object[] array, int begin, int end) {
		assert 0 <= begin;
		assert begin < end;
		assert end < array.length;

		for (int i = begin; i <= end; i++) {
			int r = nextInt(i, end);
			Object tmp = array[i];
			array[i] = array[r];
			array[r] = tmp;
		}
	}

	@Override
	public void shuffle(List<?> list) {
		shuffle(list, list.size());
	}

	@Override
	public void shuffle(List<?> list, int n) {
		shuffle(list, 0, n - 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void shuffle(List<?> list, int begin, int end) {
		int size = end - begin + 1;
		if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
			@SuppressWarnings("rawtypes")
			final List l = list;
			for (int i = begin; i <= end; i++) {
				int r = nextInt(i, end);
				l.set(r, l.set(i, l.get(r)));
			}
        } else {
            Object[] ary = list.subList(begin, end).toArray();
            shuffle(ary);
            @SuppressWarnings("rawtypes")
						ListIterator it = list.listIterator();
            for (int i = 0; i < begin; i++) {
                it.next();
            }
            for (int i = 0; i < size; i++) {
                it.next();
                it.set(ary[i]);
            }
        }
	}
	private static final int SHUFFLE_THRESHOLD = 5;	// see java.util.Collections

}
