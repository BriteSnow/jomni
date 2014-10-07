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
			//String key = objs[i].toString();
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

	static final public <T>  Set<T> setOf(T... objs){
		Set<T> set = new HashSet<>();
		Collections.addAll(set, objs);
		return set;
	}

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
