package org.jomni;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A Simple Object or Map Wrapper that normalize object property access. Modeled after Map interface (Subset for now).
 *
 * The only way to get it right now, is to use
 */
public class Omni<T> {

	public enum SetterRule {
		not_nulls, no_override,
	}

	private final Object obj;
	private final Map map;
	private final ClassInfo classInfo;
	private final JomniMapper mapper;

	Omni(T o, JomniMapper mapper){
		if (o instanceof Map){
			map = (Map)o;
			obj = null;
		}else{
			map = null;
			obj = o;
		}
		this.mapper = mapper;
		classInfo = (o != null)?mapper.getClassInfo(o.getClass()):null;
	}


	public T get(){
		return (T) ((obj != null)?obj:map);
	}

	// --------- Transformation --------- //
	public Map asMap(){
		return mapper.asMap(get());
	}

	public <R> R as(Class<R> targetClass) {
		return mapper.as(targetClass, get());
	}

	public <R> R into(Supplier<R> supplier) {
		R target = supplier.get();
		mapper.omni(target).setAll(get());
		return target;
	}
	public <R> R into(R target) {
		mapper.omni(target).setAll(get());
		return target;
	}
	// --------- /Transformation --------- //

	// --------- Setters --------- //
	public Omni<T> setAll(Object sourceObj){
		// if this object is null or the sourceObj is null, then, do nothing and return this.
		if (classInfo == null || sourceObj == null){
			return this;
		}

		Omni jSource = (sourceObj instanceof Omni)?(Omni)sourceObj:mapper.omni(sourceObj);
		Class targetClass = classInfo.getObjectClass();

		// determining prop name
		Set<String> propNames;
		// if the target is a Map, then, all the source properties need to be set
		if (Map.class.isAssignableFrom(targetClass)){
			propNames = jSource.keySet();
		}
		// otherwise, if the target is an object, propNames is the intersection of target and source
		else{
			// make sure to copy
			propNames = new HashSet<>(keySet());
			propNames.retainAll(jSource.keySet());
		}

		for (String propName : propNames) {
			Object value = jSource.get(propName);
			set(propName,value);
		}
		return this;
	}

	public Omni<T> set(String name, Object value){
		put(name, value);
		return this;
	}
	// --------- /Setters --------- //

	// --------- Property Extra Methods --------- //
	/**
	 *
	 * @param name
	 * @return Return the type of the property matching this name (only if it is an object). Return null if it is a map or if prop does not exist.
	 */
	public Class getType(String name){
		if (map != null){
			return null;
		}
		PropInfo propInfo = classInfo.getPropInfo(name);
		if (propInfo != null){
			return propInfo.getType();
		}else{
			return null;
		}
	}
	// --------- /Property Extra Methods --------- //

	// --------- Map Like Methods --------- //
	public Object get(String name){
		if (map != null){
			return map.get(name);
		}else {
			return classInfo.getValue(obj, name);
		}
	}

	public void put(String name, Object value){
		if (map != null){
			map.put(name,value);
		}else{
			Class propType = getType(name);
			// Convert only if we have a propTargetType (otherwise, it is a map value, so, as is)
			Object propValue = (propType != null)?mapper.as(propType, value):value;
			classInfo.setValue(obj, name, propValue);
		}
	}

	public Set<String> keySet(){
		if (map != null){
			return map.keySet();
		}else{
			return classInfo.getPropertyNames();
		}
	}

	public boolean containsKey(String key) {
		if (map != null) {
			return map.containsKey(key);
		}else {
			return classInfo.hasProperty(key);
		}
	}
	// --------- /Map Like Methods --------- //

}
