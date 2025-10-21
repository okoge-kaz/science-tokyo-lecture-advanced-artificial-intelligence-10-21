package jssf.di;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * A factory of any objects that instantiates with the experimental setting properties.
 *
 * @since 2
 * @author isao
 */
public class TCObjectFactory {

	TCProperties fProperties;

	HashMap<String, Object> fObjectPool;
	
	HashMap<Object, String> fKeyPool;

	/**
	 * Creates an object factory.
	 * @param pathOfProperties
	 * @throws Exception
	 * @since 2 hmkz
	 */
	public TCObjectFactory(String pathOfProperties) throws Exception {
		fProperties = new TCProperties();
		fProperties.load(pathOfProperties);
		fObjectPool = new HashMap<String, Object>();
		fKeyPool = new HashMap<Object, String>();
	}

	/**
	 * Creates an object factory.
	 * @param props
	 * @since 2 hmkz
	 */
	public TCObjectFactory(TCProperties props) {
		fProperties = props;
		fObjectPool = new HashMap<String, Object>();
		fKeyPool = new HashMap<Object, String>();
	}
	
	/**
	 * オブジェクトファクトリが生成したオブジェクトに対応するキーを返す．
	 * @param o オブジェクト
	 * @return キー．oが，このファクトリによって作成されたものでなければ，nullを返す。
	 */
	public String getKey(Object o) {
		return fKeyPool.get(o);
	}

	public <T> T create(String key) throws Exception {
		String uniqueKey = fProperties.unifyKey(key);
		if (!fObjectPool.containsKey(uniqueKey)) {
			if (uniqueKey == null) {
				String absKey = fProperties.getAbsoluteKey(key);
				throw new RuntimeException("The key '"+ absKey + "' is not found in the property file!!");
			}
			Set<String> s = getPropertyKeysForCurrentObject(uniqueKey, fProperties);
			Class<?> c = Class.forName(fProperties.getProperty(uniqueKey));
			TCConstructor constructor = getMatchedConstructor(c, s);
			String baseKey = fProperties.getBaseKey();
			fProperties.setBaseKey(uniqueKey + ".");
			Object o = constructor.newInstance(this);
			fObjectPool.put(uniqueKey, o);
			fKeyPool.put(o, uniqueKey);
			fProperties.setBaseKey(baseKey);
		}
		@SuppressWarnings("unchecked")
		T instance = (T) fObjectPool.get(uniqueKey);
		return instance;
	}

	public <T> T create(String key, String defaultValue) throws Exception {
		String absKey = fProperties.getAbsoluteKey(key);
		if (fProperties.getProperty(absKey) == null) {
			fProperties.put(absKey, defaultValue);
		}
		String uniqueKey = fProperties.unifyKey(key);
		if (!fObjectPool.containsKey(uniqueKey)) {
			if (uniqueKey == null) {
				throw new RuntimeException("The key '"+ absKey + "' is not found in the property file!!");
			}
			Set<String> s = getPropertyKeysForCurrentObject(uniqueKey, fProperties);
			Class<?> c = Class.forName(fProperties.getProperty(uniqueKey));
			TCConstructor constructor = getMatchedConstructor(c, s);
			String baseKey = fProperties.getBaseKey();
			fProperties.setBaseKey(uniqueKey + ".");
			Object o = constructor.newInstance(this);
			fObjectPool.put(uniqueKey, o);
			fKeyPool.put(o, uniqueKey);
			fProperties.setBaseKey(baseKey);
		}
		@SuppressWarnings("unchecked")
		T instance = (T) fObjectPool.get(uniqueKey);
		return instance;
	}

	public <T> T[] createArray(String key, Class<?> componentType) throws Exception {
		String absKey = fProperties.getAbsoluteKey(key);
		String val = fProperties.getProperty(absKey);
		if (val == null) {
			throw new RuntimeException("The key '"+ absKey + "' is not found in the property file!!");
		}
		StringTokenizer st = new StringTokenizer(val);
		String[] values = new String[st.countTokens()];
		for (int i = 0; i < values.length; i++) {
			values[i] = st.nextToken();
		}
		@SuppressWarnings("unchecked")
		T[] instances = (T[]) Array.newInstance(componentType, values.length);
		for (int i = 0; i < values.length; i++) {
			if (values[i].charAt(0) != '$') {
				Set<String> s = getPropertyKeysForCurrentObject(absKey, fProperties);
				Class<?> c = Class.forName(values[i]);
				TCConstructor constructor = getMatchedConstructor(c, s);
				String baseKey = fProperties.getBaseKey();
				fProperties.setBaseKey(absKey + ".");
				instances[i] = constructor.newInstance(this);
				fProperties.setBaseKey(baseKey);
			} else {
				instances[i] = create(values[i]);
			}
		}
		return instances;
	}

	public <T> T[] createArray(String key, Class<?> componentType, String defaultValue) throws Exception {
		String absKey = fProperties.getAbsoluteKey(key);
		String val = fProperties.getProperty(absKey);
		if (val == null) {
			val = defaultValue;
			fProperties.put(absKey, val);
		}
		StringTokenizer st = new StringTokenizer(val);
		String[] values = new String[st.countTokens()];
		for (int i = 0; i < values.length; i++) {
			values[i] = st.nextToken();
		}
		@SuppressWarnings("unchecked")
		T[] instances = (T[]) Array.newInstance(componentType, values.length);
		for (int i = 0; i < values.length; i++) {
			if (values[i].charAt(0) != '$') {
				Set<String> s = getPropertyKeysForCurrentObject(absKey, fProperties);
				Class<?> c = Class.forName(values[i]);
				TCConstructor constructor = getMatchedConstructor(c, s);
				String baseKey = fProperties.getBaseKey();
				fProperties.setBaseKey(absKey + ".");
				instances[i] = constructor.newInstance(this);
				fProperties.setBaseKey(baseKey);
			} else {
				instances[i] = create(values[i]);
			}
		}
		return instances;
	}

	private static TCConstructor getMatchedConstructor(Class<?> clazz, Set<String> propertyKeys) {
		Constructor<?>[] cons = clazz.getConstructors();
		for (Constructor<?> c : cons) {
			TCConstructor constructor = new TCConstructor(c);
			if (constructor.isParameterMatched(propertyKeys)) {
				return constructor;
			}
		}
		throw new RuntimeException("No constructor matched: " + clazz + " to " + propertyKeys);
	}

	private static Set<String> getPropertyKeysForCurrentObject(String absKey, TCProperties props) {
		Set<String> propertyset = new HashSet<String>();
		Set<String> allkeys = props.stringPropertyNames();
		int begin = absKey.length() + 1;
		for (String key : allkeys) {	//プロパティの中から現在のオブジェクト用のプロパティを検索する
			if (key.startsWith(absKey) && !key.equals(absKey) && key.charAt(absKey.length()) == '.') {
				int end = key.indexOf('.', begin);
				if (end == -1) {	//'.'が登場しなければ，ベースキーから末尾までがプロパティ・キー
					propertyset.add(key.substring(begin));
				} else {	//'.'が登場するならば，ベースキーから'.'までがプロパティ・キー
					propertyset.add(key.substring(begin, end));
				}
			}
		}
		return propertyset;
	}

}

class TCConstructor {
	private Constructor<?> fConstructor;
	private TCParameter[] fParams;

	public TCConstructor(Constructor<?> c) {
		fConstructor = c;
		Class<?>[] paramTypes = fConstructor.getParameterTypes();
		Annotation[][] paramAnos = fConstructor.getParameterAnnotations();
		fParams = new TCParameter[paramTypes.length];
		for (int i = 0; i < fParams.length; i++) {
			String key = null;
			String defaultValue = null;
			for (Annotation a : paramAnos[i]) {
				if (a instanceof ACParam) {
					key = ((ACParam) a).key();
					defaultValue = ((ACParam) a).defaultValue();
					break;
				}
			}
			fParams[i] = new TCParameter(paramTypes[i], key, defaultValue);
		}
	}

	public TCParameter getParameter(int i) {
		return fParams[i];
	}

	public int getNoOfParameters() {
		return fParams.length;
	}

	public <T> T newInstance(TCObjectFactory factory) throws Exception {
		Object[] args = new Object[fParams.length];
		TCProperties props = factory.fProperties;
		HashMap<String, Object> map = factory.fObjectPool;
		for (int i = 0; i < args.length; i++) {
			String key = fParams[i].getKey();
			Class<?> clazz = fParams[i].getType();
			String defaultValue = fParams[i].getDefaultValue();
			String absKey = props.getAbsoluteKey(key);
			if (props.getProperty(absKey) == null) {
				props.put(absKey, defaultValue);
			}
			String uniqueKey = props.unifyKey(key);
			if (!map.containsKey(uniqueKey)) {
				if (clazz == byte.class) {
					map.put(uniqueKey, props.getByteProperty(key));
				} else if (clazz == short.class) {
					map.put(uniqueKey, props.getShortProperty(key));
				} else if (clazz == int.class) {
					map.put(uniqueKey, props.getIntProperty(key));
				} else if (clazz == long.class) {
					map.put(uniqueKey, props.getLongProperty(key));
				} else if (clazz == float.class) {
					map.put(uniqueKey, props.getFloatProperty(key));
				} else if (clazz == double.class) {
					map.put(uniqueKey, props.getDoubleProperty(key));
				} else if (clazz == char.class) {
					map.put(uniqueKey, props.getCharProperty(key));
				} else if (clazz == boolean.class) {
					map.put(uniqueKey, props.getBooleanProperty(key));
				} else if (clazz == String.class) {
					map.put(uniqueKey, props.getStringProperty(key));
				} else if (clazz == byte[].class) {
					map.put(uniqueKey, props.getByteArrayProperty(key));
				} else if (clazz == short[].class) {
					map.put(uniqueKey, props.getShortArrayProperty(key));
				} else if (clazz == int[].class) {
					map.put(uniqueKey, props.getIntArrayProperty(key));
				} else if (clazz == long[].class) {
					map.put(uniqueKey, props.getLongArrayProperty(key));
				} else if (clazz == float[].class) {
					map.put(uniqueKey, props.getFloatArrayProperty(key));
				} else if (clazz == double[].class) {
					map.put(uniqueKey, props.getDoubleArrayProperty(key));
				} else if (clazz == char[].class) {
					map.put(uniqueKey, props.getCharArrayProperty(key));
				} else if (clazz == boolean[].class) {
					map.put(uniqueKey, props.getBooleanArrayProperty(key));
				} else if (clazz == String[].class) {
					map.put(uniqueKey, props.getStringArrayProperty(key));
				} else if (clazz.isArray()) {
					Class<?> ctype = clazz.getComponentType();
					if (ctype.isEnum()) {
						String[] keys = props.getStringArrayProperty(key);
						Object[] ary = (Object[]) Array.newInstance(ctype, keys.length);
						for (int j = 0; j < ary.length; j++) {
							@SuppressWarnings({ "unchecked", "rawtypes" })
							Object e = Enum.valueOf((Class)ctype, keys[j]);
							ary[j] = e;
						}
						map.put(uniqueKey, ary);
					} else {
						map.put(uniqueKey, factory.createArray(key, ctype));
					}
				} else {
					if (clazz.isEnum()) {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						Object e = Enum.valueOf((Class)clazz, props.getStringProperty(key));
						map.put(uniqueKey, e);
					} else {
						map.put(uniqueKey, factory.create(key));
					}
				}
			}
			args[i] = map.get(uniqueKey);
		}
		@SuppressWarnings("unchecked")
		T instance = (T) fConstructor.newInstance(args);
		return instance;
	}

	/**
	 * プロパティファイルに指定されたキーがすべて見つかり，かつ，
	 * プロパティファイルに指定されなかったキーのすべてにデフォルト値が設定されている場合のみtrue
	 * そうでなければfalseを返す．
	 *
	 * @param propertyKeys
	 * @return パラメータがマッチしたか
	 * @since 2 hmkz
	 */
	public boolean isParameterMatched(Set<String> propertyKeys) {
		final int n = getNoOfParameters();
		for (TCParameter param : fParams) {
			if (param.getKey() == null) {
				//ACParamが付加されていない引数が存在したらfalse
				return false;
			}
			if (!propertyKeys.contains(param.getKey()) && param.getDefaultValue().isEmpty()) {
				//プロパティファイルに指定されなかったキーにはデフォルト値が設定されていなければならない．
				return false;
			}
		}
		for (String propKey : propertyKeys) {
			boolean isParameterFound = false;
			for (int i = 0; i < n; i++) {
				if (fParams[i].getKey().equals(propKey)) {
					isParameterFound = true;
					break;
				}
			}
			if (!isParameterFound) {//プロパティファイルに指定されたキーは必ず存在しなければいけない．
				return false;
			}
		}
		return true;
	}

}

class TCParameter {
	private Class<?> fType;
	private String fKey;
	private String fDefaultValue;

	public TCParameter(Class<?> type, String key, String defaultValue) {
		fType = type;
		fKey = key;
		fDefaultValue = defaultValue;
	}

	public Class<?> getType() {
		return fType;
	}

	public String getKey() {
		return fKey;
	}

	public String getDefaultValue() {
		return fDefaultValue;
	}

}
