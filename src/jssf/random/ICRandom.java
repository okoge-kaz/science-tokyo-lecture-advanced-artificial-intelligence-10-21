package jssf.random;

import java.io.Serializable;
import java.util.List;

/**
 * A (pseudo) random number generator.
 *
 * @since 2
 * @author hmkz
 */
public interface ICRandom extends Serializable {

	/**
	 * Sets the seed of random number sequence.
	 *
	 * @param seed
	 * @since 2 hmkz
	 */
	void setSeed(long seed);

    /**
     * Returns a {@code boolean} value uniformly distributed on the range of {@code boolean}
     * using the next value of the random number sequence in this object.
     *
     * @return a random {@code boolean} value
     * @since 2 hmkz
     */
	boolean nextBoolean();

    /**
     * Returns a {@code boolean} value distributed on the range of {@code boolean}
     * using the next value of the random number sequence in this object.
     * The probability to return {@code true} and {@code false} is (approximately) {@code pTrue}
     * and {@code 1 - pTrue}, respectively.  We necessarily ensure the following two conditions;
     * <ul>
     *  <li>if {@code pTrue >= 1.0d}, then {@code true} is always returned.
     *  <li>if {@code pTrue <= 0.0d}, then {@code false} is always returned.
     * </ul>
     *
	 * @param pTrue the probability to return {@code true}
     * @return a random {@code boolean} value
     * @since 30 hmkz
     */
	boolean nextBoolean(double pTrue);

	/**
	 * Returns a {@code char} value uniformly distributed on the range of {@code char}
	 * using the next value of the random number sequence in this object.
	 *
	 * @return a random {@code char} value
	 * @since 27 hmkz
	 */
	char nextChar();

	/**
	 * Returns a {@code char} value uniformly distributed on the closed interval {@code [0, n-1]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param n the number of possible values
	 * @return a random {@code char} value
	 * @since 27 hmkz
	 */
	char nextChar(char n);

	/**
	 * Returns a {@code char} value uniformly distributed on the closed interval {@code [min, max]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param min the possible minimum value to be returned
	 * @param max the possible maximum value to be returned
	 * @return a random {@code char} value
	 * @since 27 hmkz
	 */
	char nextChar(char min, char max);

	/**
	 * Returns a {@code byte} value uniformly distributed on the range of {@code byte}
	 * using the next value of the random number sequence in this object.
	 *
	 * @return a random {@code byte} value
	 * @since 27 hmkz
	 */
	byte nextByte();

	/**
	 * Returns a {@code byte} value uniformly distributed on the closed interval {@code [0, n-1]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param n the number of possible values
	 * @return a random {@code byte} value
	 * @since 27 hmkz
	 */
	byte nextByte(byte n);

	/**
	 * Returns a {@code byte} value uniformly distributed on the closed interval {@code [min, max]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param min the possible minimum value to be returned
	 * @param max the possible maximum value to be returned
	 * @return a random {@code byte} value
	 * @since 27 hmkz
	 */
	byte nextByte(byte min, byte max);

	/**
	 * Returns a {@code short} value uniformly distributed on the range of {@code short}
	 * using the next value of the random number sequence in this object.
	 *
	 * @return a random {@code short} value
	 * @since 27 hmkz
	 */
	short nextShort();

	/**
	 * Returns a {@code short} value uniformly distributed on the closed interval {@code [0, n-1]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param n the number of possible values
	 * @return a random {@code short} value
	 * @since 27 hmkz
	 */
	short nextShort(short n);

	/**
	 * Returns a {@code short} value uniformly distributed on the closed interval {@code [min, max]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param min the possible minimum value to be returned
	 * @param max the possible maximum value to be returned
	 * @return a random {@code short} value
	 * @since 27 hmkz
	 */
	short nextShort(short min, short max);

	/**
	 * Returns an {@code int} value uniformly distributed on the range of {@code int}
	 * using the next value of the random number sequence in this object.
	 *
	 * @return a random {@code int} value
	 * @since 2 hmkz
	 */
	int nextInt();

	/**
	 * Returns an {@code int} value uniformly distributed on the closed interval {@code [0, n-1]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param n the number of possible values
	 * @return a random {@code int} value
	 * @since 2 hmkz
	 */
	int nextInt(int n);

	/**
	 * Returns an {@code int} value uniformly distributed on the closed interval {@code [min, max]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param min the possible minimum value to be returned
	 * @param max the possible maximum value to be returned
	 * @return a random {@code int} value
	 * @since 2 hmkz
	 */
	int nextInt(int min, int max);

	/**
	 * Returns a {@code long} value uniformly distributed on the range of {@code long}
	 * using the next value of the random number sequence in this object.
	 *
	 * @return a random {@code long} value
	 * @since 2 hmkz
	 */
	long nextLong();

	/**
	 * Returns a {@code long} value uniformly distributed on the closed interval {@code [0, n-1]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param n the number of possible values
	 * @return a random {@code long} value
	 * @since 2 hmkz
	 */
	long nextLong(long n);

	/**
	 * Returns a {@code long} value uniformly distributed on the closed interval {@code [min, max]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param min the possible minimum value to be returned
	 * @param max the possible maximum value to be returned
	 * @return a random {@code long} value
	 * @since 2 hmkz
	 */
	long nextLong(long min, long max);

	/**
	 * Returns a {@code float} value uniformly distributed on the closed-open interval {@code [0.0f, 1.0f)}
	 * using the next value of the random number sequence in this object.
	 *
	 * @return a random {@code float} value
	 * @since 2 hmkz
	 */
	float nextFloat();

	/**
	 * Returns a {@code float} value uniformly distributed on the closed-open interval {@code [0.0f, sup)}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param sup the supremum of random numbers (exclusively)
	 * @return a random {@code float} value
	 * @since 30 hmkz
	 */
	float nextFloat(float sup);

	/**
	 * Returns a {@code float} value uniformly distributed on the closed interval {@code [min, max]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param min the possible minimum value to be returned
	 * @param max the possible maximum value to be returned
	 * @return a random {@code float} value
	 * @since 2 hmkz
	 */
	float nextFloat(float min, float max);

	/**
	 * Returns a {@code double} value uniformly distributed on the closed-open interval {@code [0.0d, 1.0d)}
	 * using the next value of the random number sequence in this object.
	 *
	 * @return a random {@code double} value
	 * @since 2 hmkz
	 */
	double nextDouble();

	/**
	 * Returns a {@code double} value uniformly distributed on the closed-open interval {@code [0.0d, sup)}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param sup the supremum of random numbers (exclusively)
	 * @return a random {@code double} value
	 * @since 30 hmkz
	 */
	double nextDouble(double sup);

	/**
	 * Returns a {@code double} value uniformly distributed on the closed interval {@code [min, max]}
	 * using the next value of the random number sequence in this object.
	 *
	 * @param min the possible minimum value to be returned
	 * @param max the possible maximum value to be returned
	 * @return a random {@code double} value
	 * @since 2 hmkz
	 */
	double nextDouble(double min, double max);

	/**
	 * Returns a {@code double} value on the standard Gaussian distribution
	 * (i.e. its mean is 0 and standard deviation is 1).
	 *
	 * @return a random {@code double} value
	 * @since 2 hmkz
	 */
	double nextGaussian();

	/**
	 * Returns a {@code double} value on the Gaussian distribution with the specified parameters; {@code mu} and {@code sigma}.
	 *
	 * @param mu the mean of Gaussian distribution
	 * @param sigma the standard deviation of Gaussian distribution (must be non-negative)
	 * @return a random {@code double} value
	 * @since 2 hmkz
	 */
	double nextGaussian(double mu, double sigma);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the range of {@code boolean}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @since 27 hmkz
	 */
	void fill(boolean[] buf);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated so that the probability to generate {@code true} and {@code false} is
	 * (approximately) {@code pTrue} and {@code 1 - pTrue}, respectively.  We necessarily
	 * ensure the following two conditions;
     * <ul>
     *  <li>if {@code pTrue >= 1.0d}, then all the elements of the resulting array {@code buf} are {@code true}.
     *  <li>if {@code pTrue <= 0.0d}, then all the elements of the resulting array {@code buf} are {@code false}.
     * </ul>
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param pTrue the probability to generate {@code true}
	 * @since 30 hmkz
	 */
	void fill(boolean[] buf, double pTrue);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the range of {@code char}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @since 27 hmkz
	 */
	void fill(char[] buf);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [0, n-1]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param n the number of possible values
	 * @since 27 hmkz
	 */
	void fill(char[] buf, char n);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [min, max]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param min the possible minimum value to be generated
	 * @param max the possible maximum value to be generated
	 * @since 27 hmkz
	 */
	void fill(char[] buf, char min, char max);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the range of {@code byte}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @since 27 hmkz
	 */
	void fill(byte[] buf);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [0, n-1]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param n the number of possible values
	 * @since 27 hmkz
	 */
	void fill(byte[] buf, byte n);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [min, max]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param min the possible minimum value to be generated
	 * @param max the possible maximum value to be generated
	 * @since 27 hmkz
	 */
	void fill(byte[] buf, byte min, byte max);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the range of {@code short}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @since 27 hmkz
	 */
	void fill(short[] buf);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [0, n-1]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param n the number of possible values
	 * @since 27 hmkz
	 */
	void fill(short[] buf, short n);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [min, max]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param min the possible minimum value to be generated
	 * @param max the possible maximum value to be generated
	 * @since 27 hmkz
	 */
	void fill(short[] buf, short min, short max);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the range of {@code int}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @since 27 hmkz
	 */
	void fill(int[] buf);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [0, n-1]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param n the number of possible values
	 * @since 27 hmkz
	 */
	void fill(int[] buf, int n);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [min, max]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param min the possible minimum value to be generated
	 * @param max the possible maximum value to be generated
	 * @since 2 hmkz
	 */
	void fill(int[] buf, int min, int max);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the range of {@code long}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @since 27 hmkz
	 */
	void fill(long[] buf);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [0, n-1]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param n the number of possible values
	 * @since 27 hmkz
	 */
	void fill(long[] buf, long n);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [min, max]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param min the possible minimum value to be generated
	 * @param max the possible maximum value to be generated
	 * @since 27 hmkz
	 */
	void fill(long[] buf, long min, long max);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed-open interval {@code [0.0f, 1.0f)}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @since 27 hmkz
	 */
	void fill(float[] buf);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed-open interval {@code [0.0f, sup)}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param sup the supremum of random numbers (exclusively)
	 * @since 30 hmkz
	 */
	void fill(float[] buf, float sup);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [min, max]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param min the possible minimum value to be generated
	 * @param max the possible maximum value to be generated
	 * @since 27 hmkz
	 */
	void fill(float[] buf, float min, float max);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed-open interval {@code [0.0d, 1.0d)}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @since 27 hmkz
	 */
	void fill(double[] buf);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed-open interval {@code [0.0d, sup)}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param sup the supremum of random numbers (exclusively)
	 * @since 30 hmkz
	 */
	void fill(double[] buf, double sup);

	/**
	 * Fills the specified array {@code buf} with random numbers.  The random numbers are
	 * generated (approximately) uniform-randomly on the closed interval {@code [min, max]}.
	 *
	 * @param buf a buffer to be filled with random numbers
	 * @param min the possible minimum value to be generated
	 * @param max the possible maximum value to be generated
	 * @since 2 hmkz
	 */
	void fill(double[] buf, double min, double max);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 27 hmkz
	 */
	void shuffle(boolean[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(boolean[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(boolean[] a, int begin, int end);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 27 hmkz
	 */
	void shuffle(char[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(char[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(char[] a, int begin, int end);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 27 hmkz
	 */
	void shuffle(byte[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(byte[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(byte[] a, int begin, int end);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 27 hmkz
	 */
	void shuffle(short[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(short[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(short[] a, int begin, int end);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 2 hmkz
	 */
	void shuffle(int[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(int[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(int[] a, int begin, int end);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 27 hmkz
	 */
	void shuffle(long[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(long[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(long[] a, int begin, int end);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 27 hmkz
	 */
	void shuffle(float[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(float[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(float[] a, int begin, int end);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 2 hmkz
	 */
	void shuffle(double[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(double[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(double[] a, int begin, int end);

	/**
	 * Shuffles the specified array {@code a} destructively.  The order of the elements in
	 * the resulting array are guaranteed to be (approximately) uniform-random
	 * for any given array {@code a} .
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @since 27 hmkz
	 */
	void shuffle(Object[] a);

	/**
	 * Shuffles the first {@code n} elements in the specified array {@code a} destructively.
	 * For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(Object[] a, int n);

	/**
	 * Shuffles the elements of the specified array {@code a} placed between {@code begin} and {@code end}
	 * destructively.  For any given array {@code a}, the order of the elements in the resulting array are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param a an array to be shuffled.  The resulting array is returned by
	 * overwriting this array
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(Object[] a, int begin, int end);

	/**
	 * Shuffles the specified list destructively.  The order of the elements in
	 * the resulting list are guaranteed to be (approximately) uniform-random
	 * for any given list {@code list}.
	 *
	 * @param list a list to be shuffled.  The resulting list is returned by
	 * overwriting this list
	 * @since 27 hmkz
	 */
	void shuffle(List<?> list);

	/**
	 * Shuffles the first {@code n} elements in the specified list destructively.
	 * For any given list {@code list}, the order of the elements in the resulting list are
	 * guaranteed that the first {@code n} elements' is (approximately) uniform-random
	 * and the others' equals to the original's.
	 *
	 * @param list a list to be shuffled.  The resulting list is returned by
	 * overwriting this list
	 * @param n the number of elements to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(List<?> list, int n);

	/**
	 * Shuffles the elements of the specified list placed between {@code begin} and {@code end}
	 * destructively.  For any given list {@code list}, the order of the elements in the resulting list are
	 * guaranteed that the elements' between {@code begin} and {@code end} is (approximately)
	 * uniform-random and the others' equals to the original's.
	 *
	 * @param list a list to be shuffled.  The resulting list is returned by
	 * overwriting this list
	 * @param begin the index of the first element to be shuffled
	 * @param end the index of the last element to be shuffled
	 * @since 27 hmkz
	 */
	void shuffle(List<?> list, int begin, int end);

}
