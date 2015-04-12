/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.jomni.util;

import java.util.*;

public class Maps {

	public static class NamesValues{
		public final String[] names;
		public final Object[] values;

		public NamesValues(String[] names, Object[] values) {
			this.names = names;
			this.values = values;
		}
	}

	/**
	 * <p>Create a HashMap of a list of [name,value,name,value] array.</p>
	 *
	 * <p>If the array miss the last "value" for the last "name" then the value will be set to null for this property.</p>
	 *
	 */
	static final public Map mapOf(Object... objs) {
        HashMap<Object, Object> m = new HashMap<>();

        for (int i = 0; i < objs.length; i += 2) {
			Object key = objs[i];
			if (i + 1 < objs.length) {
				Object value = objs[i + 1];
				m.put(key, value);
			} else {
				m.put(key, null);
			}
		}
		return m;
    }

	/**
	 * <p>Similar to mapOf, but will get the string from the keys, to return a Map<String,Object></p>
	 * @param objs
	 * @return
	 */
	static final public Map<String,Object> propMapOf(Object... objs) {
		HashMap<String, Object> m = new HashMap<>();

		for (int i = 0; i < objs.length; i += 2) {
			//String key = objs[i].toString();
			Object keyObj = objs[i];
			String key = (keyObj instanceof String)?(String)keyObj:keyObj.toString();
			if (i + 1 < objs.length) {
				Object value = objs[i + 1];
				m.put(key, value);
			} else {
				m.put(key, null);
			}
		}
		return m;
	}

	static final public <T>  Set<T> setOf(T... objs){
		Set<T> set = new HashSet<>();
		Collections.addAll(set, objs);
		return set;
	}

	// --------- Nested Maps --------- //

	/**
	 * <p>Nest the flattenMap following the "." notation scheme.</p>
	 *
	 * <p>A flattenMap with an entry like {"company.name":"Nike"} will result in a entry in the returned map like {"company":{"name":"Nike}}.</p>
	 * @param flattenMap
	 * @return
	 */
	static final public Map<String, Object> asNestedMap(Map<? extends Object, ? extends Object> flattenMap){
		Map<String, Object> nestedPropMap = new HashMap<>();

		Map<String, Map<String,Object>> mapByPath = new HashMap<>();

		for (Map.Entry entry : flattenMap.entrySet()){
			Object keyObj = entry.getKey();
			String key = (keyObj instanceof String)?(String)keyObj:keyObj.toString();

			Object val = entry.getValue();

			Object propVal = val;
			Map<String, Object> childPropMap = null;

			if (key.indexOf('.') != -1) {
				String[] names = key.split("\\.");
				int lastIdx = names.length - 1;
				for (int i = lastIdx; i >= 0; i--) {
					String propName = names[i];

					// the mapByPath key (TODO: need to optimize this)
					String mapByPathKey = String.join(".",Arrays.copyOfRange(names, 0, i));

					// if i == 0, then, we take the base propMap
					Map<String, Object> propMap = (i == 0)?nestedPropMap:mapByPath.get(mapByPathKey);

					boolean newPropMap = false;
					// create the propMap if needed
					if (propMap == null) {
						propMap = new HashMap<>();
						mapByPath.put(mapByPathKey, propMap);
						newPropMap = true;
					}

					// put the value
					propMap.put(propName, propVal);

					// determine if we need set childPropMap for next iteration
					if (newPropMap){
						propVal = propMap;
					}else{
						break;
					}
				}


			}else{
				nestedPropMap.put(key,val);
			}
		}

		return nestedPropMap;
	}

	/**
	 * <p>Flatten nested map with a single level Map following the "." notation scheme.</p>
	 *
	 * <p>An entry in the nestedMap like {"company":{"name":"Nike}}, will result in a flattenMap entry like {"company.name":"Nike"}.</p>
	 *
	 * @param nestedMap
	 * @return
	 */
	static final public Map<String,Object> asFlatMap(Map<? extends Object,? extends Object> nestedMap){
		Map flattenPropMap = new HashMap();

		List<Pair<String,Map<Object,Object>>> maps = new ArrayList<>();
		maps.add(new Pair("",nestedMap));

		while (maps != null) {
			List<Pair<String, Map<Object, Object>>> newMaps = new ArrayList<>();

			for (Pair<String, Map<Object, Object>> pathAndMap : maps) {
				String basePath = pathAndMap.getA();
				Map<Object, Object> m = pathAndMap.getB();

				// One level property/value
				for (Map.Entry entry : m.entrySet()) {

					Object keyObj = entry.getKey();
					String key = (keyObj instanceof String) ? (String) keyObj : keyObj.toString();
					Object value = entry.getValue();

					String propName = basePath + key;

					if (value instanceof Map) {
						newMaps.add(new Pair(propName + ".", (Map) value));
					} else {
						flattenPropMap.put(propName, value);
					}
				}
			}

			if (newMaps.size() > 0){
				maps = newMaps;
			}else{
				maps = null;
			}

		}


		return flattenPropMap;
	}

	static final public Object nestedValue(Map map, String path){
		Object val = null;

		String[] names = path.split("\\.");
		Map m = map;
		int lastIdx = names.length - 1;
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			// assume the map have string Keys
			Object v = m.get(name);
			if (i == lastIdx){
				// if it is the last index, then, just return the value.
				val = v;
				break;
			}else{

				if (v instanceof Map){
					// if it not the last index, and it is v is a Map, then, we continue
					m = (Map)v;
				}else{
					// otherwise, we break (Omni object will add support for typed object, but here, just support plain map)
					break;
				}
			}
		}
		return val;
	}

	// --------- /Nested Maps --------- //

	/**
	 * Return the [names,values] ([String[],Object[]]) for the key/value of the map.
	 *
	 * Note the the key is converted to String with key.toString()
	 *
	 * For example, for a map = {name1:val1,name2:val2}
	 * getNamesAndValues(map) == [["name1","name2"],[val1,val2]];
	 *
	 * @param map the name,value pair to create the value for. If null, return empty NamesValues.
	 *
	 * @return [names,values] as [String[],Object[]]
	 */
	static final public NamesValues namesValuesOf(Map map){
		if (map == null){
			return new NamesValues(new String[0],new Object[0]);
		}

		Object[] keys = map.keySet().toArray();
		String[] names = new String[keys.length];
		Object[] vals = new Object[keys.length];

		for (int i = 0 ; i < vals.length; i++){
			Object key = keys[i];
			names[i] = keys[i].toString();
			vals[i] = map.get(keys[i]);
		}
		return new NamesValues(names,vals);
	}
}
