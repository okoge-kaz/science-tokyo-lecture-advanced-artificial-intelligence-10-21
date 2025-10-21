package jssf.di;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Properties;

import jssf.util.TCCharsetDetector;


/**
 * Extended Properties class that additionally supports:
 * <ul>
 * <li>to return casted values (<tt>int, long, double, boolean, String</tt>, and their arrays).
 * <li>to specify a value by using the key absolutely (called <i>absolute key</i>),
 *     or relatively (called <i>relative key</i>) from the current <i>base key</i>.
 * <li>to enable values to refer to the other values using <i>binding form</i> (e.g. <tt>SomeKey=$OtherKey</tt>)
 * </ul>
 *
 * @since 2
 * @author isao
 */
public class TCProperties extends Properties implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	/** The prefix for relative keys */
	private String fBaseKey = "";

	/**
	 * Constructs an instance.
	 * @since 2 isao
	 */
	public TCProperties() {
	}

	/**
	 * A copy constructor. Note that this method performs shallow copy of keys, values, fBaseKey and fContents.
	 * @param src an source object
	 * @since 2 isao
	 */
	public TCProperties(TCProperties src) {
		synchronized (src) {
			putAll(src);
			fBaseKey = src.fBaseKey;
		}
	}
	
	/**
	 * コンストラクタ
	 * @param propertiesFilename プロパティファイル
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public TCProperties(String propertiesFilename) throws FileNotFoundException, IOException {
		load(propertiesFilename);
	}

	/*
	 * (非 Javadoc)
	 * @see java.util.Hashtable#clone()
	 */
	@Override
	public TCProperties clone() {
		return new TCProperties(this);
	}

	/**
	 * Copies src object to itself. Note that this method performs shallow copy of keys, values, fBaseKey and fContents.
	 * @param src an source object
	 * @return a copy of this
	 * @since 2 isao
	 */
	public TCProperties copyFrom(TCProperties src) {
		synchronized (src) {
			putAll(src);
			fBaseKey = src.fBaseKey;
			return this;
		}
	}

	/**
	 * Loads the contents of this properties from the specified file <tt>filename</tt>.
	 * @param filename the file name to be read
	 * @throws FileNotFoundException when the file <tt>filename</tt> is not found
	 * @throws IOException when I/O errors occur while reading the file
	 * @since 2 isao
	 */
	public void load(String filename) throws FileNotFoundException, IOException {
		TCCharsetDetector fed = new TCCharsetDetector();
		Charset encode = fed.detectCharset(filename);
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), encode);
		load(isr);
		isr.close();
	}

	/**
	 * Returns the current base key.
	 * @return the base key
	 * @since 2 hmkz
	 */
	public String getBaseKey() {
		return fBaseKey;
	}

	/**
	 * Sets the base key.
	 * @param baseKey a new base key to be set
	 * @since 2 hmkz
	 */
	public void setBaseKey(String baseKey) {
		assert baseKey.endsWith(".");
		fBaseKey = baseKey;
	}

	/**
	 * Returns the absolute key for the specified relative <tt>key</tt>.
	 * <p>
	 * Normally, the absolute key is completed by concatenation of base key and relative key.
	 * But exceptionally when the leading character of the relative key is '$',
	 * the trailing characters are regarded as the completed absolute key themselves,
	 * ignoring the current base key.
	 * @param key a relative key to be completed to the absolute key
	 * @return the absolute key
	 * @since 2 hmkz
	 */
	public String getAbsoluteKey(String key) {
		assert ! key.isEmpty();
		return (key.charAt(0) == '$') ? key.substring(1) : fBaseKey + key;
	}

	/**
	 * 与えられたkeyの参照を解決して，末端の絶対キーを返す
	 * @param key
	 * @return 末端の絶対キー
	 * @since 2 hmkz
	 */
	public String unifyKey(String key) {
		String absKey = getAbsoluteKey(key);
		if (super.containsKey(absKey)) {
			String value = getProperty(absKey);
			if (isLeaf(value)) {
				return absKey;
			} else {
				return unifyKey(value);
			}
		} else {
			throw new RuntimeException("Found an invalid key: " + key);			
		}
		/*
		String aliasKey = findAlias(key);
		if (aliasKey != null) {
			unifyKey(aliasKey);
		}
		*/
	}

	/**
	 *
	 * @param key
	 * @return the alias of the key
	 * @since 2 hmkz
	 */
	private String findAlias(String key) {
		int i = key.lastIndexOf('.');
		if (i == -1) {
			return null;
		}
		String parentKey = key.substring(0, i);
		String parentKeyAlias = containsKey(parentKey) ? getProperty(getAbsoluteKey(parentKey)) : findAlias(parentKey);
		if (parentKeyAlias == null) {
			return null;
		}
		String keyAlias = parentKeyAlias + key.substring(i);
		return containsKey(keyAlias) ? keyAlias : findAlias(keyAlias);
	}

	private boolean isLeaf(String value) { // Modified by isao (2011/05/19)
		String[] array = value.split(" ");
		if (array.length == 1 && array[0].charAt(0) == '$') {
			return false;
		} else {
			return true;
		}
		//return value.length() == 0 || value.charAt(0) != '$';
	}

	/**
	 * Checks whether the specified <tt>key</tt> exists or not in this property list.
	 * @param key a relative key accessing to the corresponding value
	 * @return <tt>true</tt> if <tt>key</tt> exists, <tt>false</tt> otherwise
	 * @since 2 hmkz
	 */
	public boolean containsKey(String key) {
		return super.containsKey(getAbsoluteKey(key));
	}

	/**
	 * Returns the <tt>int</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>int</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public byte getByteProperty(String key) {
		return getByteProperty(key, null);
	}

	/**
	 * Returns the <tt>int</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into string representation
	 *         of <tt>int</tt> value by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>int</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>int</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public byte getByteProperty(String key, Object defaultValue) {
		return Byte.parseByte(getStringProperty(key, defaultValue));
	}

	/**
	 * Returns the <tt>int</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>int</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public short getShortProperty(String key) {
		return getShortProperty(key, null);
	}

	/**
	 * Returns the <tt>int</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into string representation
	 *         of <tt>int</tt> value by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>int</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>int</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public short getShortProperty(String key, Object defaultValue) {
		return Short.parseShort(getStringProperty(key, defaultValue));
	}

	/**
	 * Returns the <tt>int</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>int</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public int getIntProperty(String key) {
		return getIntProperty(key, null);
	}

	/**
	 * Returns the <tt>int</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into string representation
	 *         of <tt>int</tt> value by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>int</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>int</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public int getIntProperty(String key, Object defaultValue) {
		return Integer.parseInt(getStringProperty(key, defaultValue));
	}

	/**
	 * Returns the <tt>long</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>long</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public long getLongProperty(String key) {
		return getLongProperty(key, null);
	}

	/**
	 * Returns the <tt>long</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into string representation
	 *         of <tt>long</tt> value by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>long</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>long</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public long getLongProperty(String key, Object defaultValue) {
		return Long.parseLong(getStringProperty(key, defaultValue));
	}

	/**
	 * Returns the <tt>float</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>float</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public float getFloatProperty(String key) {
		return getFloatProperty(key, null);
	}

	/**
	 * Returns the <tt>float</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into string representation
	 *         of <tt>float</tt> value by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>float</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>float</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public float getFloatProperty(String key, Object defaultValue) {
		return Float.parseFloat(getStringProperty(key, defaultValue));
	}

	/**
	 * Returns the <tt>double</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>double</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public double getDoubleProperty(String key) {
		return getDoubleProperty(key, null);
	}

	/**
	 * Returns the <tt>double</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into string representation
	 *         of <tt>double</tt> value by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>double</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>double</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public double getDoubleProperty(String key, Object defaultValue) {
		return Double.parseDouble(getStringProperty(key, defaultValue));
	}

	/**
	 * Returns the <tt>double</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>double</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public char getCharProperty(String key) {
		return getCharProperty(key, null);
	}

	/**
	 * Returns the <tt>double</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into string representation
	 *         of <tt>double</tt> value by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>double</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>double</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public char getCharProperty(String key, Object defaultValue) {
		return getStringProperty(key, defaultValue).charAt(0);
	}

	/**
	 * Returns the <tt>boolean</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>boolean</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public boolean getBooleanProperty(String key) {
		return getBooleanProperty(key, null);
	}

	/**
	 * Returns the <tt>boolean</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into string representation
	 *         of <tt>boolean</tt> value by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>boolean</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>boolean</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public boolean getBooleanProperty(String key, Object defaultValue) {
		return Boolean.parseBoolean(getStringProperty(key, defaultValue));
	}

	/**
	 * Returns the <tt>String</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found, throws an exception to notify the requested value is not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @return the <tt>String</tt> value corresponding to the <tt>key</tt>
	 * @since 2 isao
	 */
	public String getStringProperty(String key) {
		return getStringProperty(key, null);
	}

	/**
	 * Returns the <tt>String</tt> value corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its value.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValue</tt> is not <tt>null</tt>,
	 * then this method returns <tt>defaultValue</tt> on instead.
	 * Otherwise, throws an exception to notify the requested value and its alternatives are not available.
	 *
	 * @param key a relative key accessing to the corresponding value
	 * @param defaultValue a default value returned when <tt>key</tt> missed.
	 *         The types that support the conversion into <tt>String</tt>
	 *         by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the <tt>String</tt> value corresponding to the <tt>key</tt>
	 *          or the <tt>String</tt> expression of <tt>defaultValue</tt>
	 * @since 2 hmkz
	 */
	public String getStringProperty(String key, Object defaultValue) {
		if (containsKey(key)) {
			String value = getProperty(getAbsoluteKey(key));
			return traverseToLeaf(value);
		}
		String aliasKey = findAlias(key);
		if (aliasKey != null) {
			String value = getProperty(getAbsoluteKey(aliasKey));
			return traverseToLeaf(value);
		}
		if (defaultValue != null) {
			System.out.println("default value used: key=" + getAbsoluteKey(key) + ", value=" + defaultValue);
			return traverseToLeaf(defaultValue.toString());
		}
		throw new RuntimeException("The key '"+ getAbsoluteKey(key) + "' and its default value are not found in the property file!!");
	}

	/**
	 * Returns an array of the <tt>short</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>short</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public byte[] getByteArrayProperty(String key, Object... defaultValues) {
		String[] values = getStringArrayProperty(key, defaultValues);
		byte[] result = new byte[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Byte.parseByte(values[i]);
		}
		return result;
	}

	/**
	 * Returns an array of the <tt>short</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>short</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public short[] getShortArrayProperty(String key, Object... defaultValues) {
		String[] values = getStringArrayProperty(key, defaultValues);
		short[] result = new short[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Short.parseShort(values[i]);
		}
		return result;
	}

	/**
	 * Returns an array of the <tt>int</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>int</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public int[] getIntArrayProperty(String key, Object... defaultValues) {
		String[] values = getStringArrayProperty(key, defaultValues);
		int[] result = new int[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Integer.parseInt(values[i]);
		}
		return result;
	}

	/**
	 * Returns an array of the <tt>long</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>long</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public long[] getLongArrayProperty(String key, Object... defaultValues) {
		String[] values = getStringArrayProperty(key, defaultValues);
		long[] result = new long[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Long.parseLong(values[i]);
		}
		return result;
	}

	/**
	 * Returns an array of the <tt>double</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>double</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public float[] getFloatArrayProperty(String key, Object... defaultValues) {
		String[] values = getStringArrayProperty(key, defaultValues);
		float[] result = new float[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Float.parseFloat(values[i]);
		}
		return result;
	}

	/**
	 * Returns an array of the <tt>double</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>double</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public double[] getDoubleArrayProperty(String key, Object... defaultValues) {
		String[] values = getStringArrayProperty(key, defaultValues);
		double[] result = new double[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Double.parseDouble(values[i]);
		}
		return result;
	}

	/**
	 * Returns an array of the <tt>double</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>double</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public char[] getCharArrayProperty(String key, Object... defaultValues) {
		String[] values = getStringArrayProperty(key, defaultValues);
		char[] result = new char[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = values[i].charAt(0);
		}
		return result;
	}

	/**
	 * Returns an array of the <tt>boolean</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>boolean</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public boolean[] getBooleanArrayProperty(String key, Object... defaultValues) {
		String[] values = getStringArrayProperty(key, defaultValues);
		boolean[] result = new boolean[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Boolean.parseBoolean(values[i]);
		}
		return result;
	}

	/**
	 * Returns an array of the <tt>String</tt> values corresponding to the specified <tt>key</tt>.
	 * If <tt>key</tt> exists in this property list, this method returns its values.
	 * If <tt>key</tt> is not found and the specified <tt>defaultValues</tt> is not <tt>null</tt>,
	 * then this method returns the array of string expressions of <tt>defaultValues</tt> on instead.
	 * Otherwise, throws an exception to notify the requested values and its alternatives are not available.
	 * The elements of the array must be written in one line and separated each other with white spaces
	 * in property definition.
	 *
	 * @param key a relative key accessing to the corresponding values
	 * @param defaultValues default values returned when <tt>key</tt> missed.
	 *         The types of each element in <tt>defaultValues</tt> that support the conversion
	 *         into <tt>String</tt> by invoking <tt>toString()</tt> method are only acceptable.
	 * @return the array of the <tt>String</tt> values corresponding to the <tt>key</tt>
	 * @since 2 hmkz
	 */
	public String[] getStringArrayProperty(String key, Object... defaultValues) {
		if (containsKey(key)) {
			String[] values = getProperty(getAbsoluteKey(key)).split("\\s");
			for (int i = 0; i < values.length; ++i) {
				values[i] = traverseToLeaf(values[i]);
			}
			return values;
		} else if (defaultValues.length != 0) {
			String[] values = new String[defaultValues.length];
			for (int i = 0; i < values.length; ++i) {
				values[i] = traverseToLeaf(defaultValues[i].toString());
			}
			return values;
		} else {
			throw new RuntimeException("The key '"+ key + "' and its default values are not found in the property file!!");
		}
	}

	/**
	 * Returns the value at the end of the binding-chain starting with the specified <tt>val</tt>.
	 * <p>
	 * If the specified <tt>val</tt> is a <i>terminal form</i> (begins with any character but '$'),
	 * then this method simply returns <tt>val</tt> itself.
	 * When <tt>val</tt> is a <i>binding form</i> (begins with '$'), this method removes leading '$' from <tt>val</tt>,
	 * and then regards it as a new key of the properties to refer the new value.
	 * This binding process is repeated recursively until finding a terminal form in traversal values,
	 * and the conclusive value of terminal form is returned.
	 *
	 * @param val a value
	 * @return the terminal value bound to the specified <tt>val</tt>
	 * @since 2 hmkz
	 */
	private String traverseToLeaf(String val) {
		// Refers to val as a new key of the properties if it begins with '$'.
		return (val.length() != 0 && val.charAt(0) == '$') ? getStringProperty(val) : val;
	}

}
