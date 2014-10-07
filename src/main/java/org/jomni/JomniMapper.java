/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.jomni;

import org.jomni.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.jomni.util.Maps.mapOf;

/**
 * <p>A custom Mapper that create a Map of propName/propValue from a Java Object without any transcoding.</p>
 * <p/>
 * <p><strong>Note: </strong> Right now, this does not support the toObj(Class,Map), so, JaSql will use the Jackson deserialization</p>
 * <p/>
 * <p><strong>Note: </strong> This mapper also consider setPropName methods that return (this) as writerMethod (i.e. chainable).
 * The standard Introspector.getBeanInfo introspection requires the writer method to return void (i.e. not chainable)</p>
 */
public class JomniMapper {

	private enum ConvertType {
		identity, none, convertor, complex;
	}

	Map<Class, ClassInfo> classInfoByClass = new ConcurrentHashMap<>(16, 0.9f, 1);

	ConvertorRegistry convertorRegistry = new ConvertorRegistry();


	/**
	 * Packaged scope constructor to force use of Builder.
	 */
	JomniMapper(ConvertorRegistry override){
		convertorRegistry.init();
		convertorRegistry.getRegistry().putAll(override.getRegistry());
	}

	/**
	 * <p>Return a Function<T,R> that will convert a value to a given type.</p>
	 *
	 * <p>Used in the Optional and Stream. For example.  stream.map(jomniMapper.to(User.class)).</p>
	 *
	 *
	 * @param targetClass
	 * @param <T>
	 * @param <R>
	 * @return
	 */
	// TODO: Need to find a way to do something like "Map m = optional.map(j.as(HashMap.class))" right now, types missmatch.
	// TODO: Function<? super T,? extends R>, still does not work (hopefully, missing something "obvious").
	public <T,R> Function<T,R> as(Class<R> targetClass){
		// TODO: should we cache this?
		return (source) -> as(targetClass,source);
	}

	/**
	 * Just a shorten for mapper.as(HashMap.class,obj)
	 * @param obj
	 * @return
	 */
	public Map<String, Object> asMap(Object obj) {
		return as(HashMap.class, obj);
	}

	/**
	 * Transform a value to a class. This can be used for complex (i.e pojo) or simple types.
	 *
	 * @param targetClass
	 * @param value
	 * @return
	 */
	private static final Map<Class,Class> implByInterface = mapOf(Map.class,HashMap.class,List.class,ArrayList.class);
	public <R, T> R as(Class<R> targetClass, T value) {
		if (value == null){
			return (R) null;
		}
		// if targetClass is an interface, try to find the implClass (if not, let it fail later)
		if (targetClass.isInterface()) {
			Class implClass = implByInterface.get(targetClass);
			targetClass = (implClass != null)?implClass:null;
		}

		Pair<ConvertType, TypeConvertor<T, R>> convertorInfo = getConvertInfo(value, targetClass);
		JomniMapper.ConvertType converterType = convertorInfo.getA();
		TypeConvertor<T, R> typeConvertor = convertorInfo.getB();
		try {
			if (converterType == ConvertType.complex) {
				R targetObject = targetClass.newInstance();
				Omni jSource = omni(value);
				Omni<R> jTarget = omni(targetObject);
				return jTarget.setAll(jSource).get();
			} else {
				return convert(converterType, typeConvertor, value, targetClass);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


	private <T, R> Pair<ConvertType, TypeConvertor<T, R>> getConvertInfo(T instance, Class<R> targetClass) {
		boolean isNull = (instance == null);

		if (instance != null && (targetClass.equals(instance.getClass()) || targetClass.isAssignableFrom(instance.getClass()))) {
			return new Pair(ConvertType.identity, null);
		}

		Class sourceClass = (isNull) ? null : instance.getClass();
		if (!isNull) {
			TypeConvertor typeConvertor = convertorRegistry.resolveConvertor(sourceClass, targetClass);
			if (typeConvertor != null) {
				return new Pair(ConvertType.convertor, typeConvertor);
			}
		}

		// now, if no type convertors and if instance is null, then, the none convert type will be applied
		if (instance == null) {
			return new Pair(ConvertType.none, null);
		}

		return new Pair(ConvertType.complex, null);
	}

	// --------- Omni Factory --------- //
	public <T> Omni<T> as(Supplier<T> supplier) {
		T obj = supplier.get();
		return omni(obj);
	}

	public <T> Omni<T> omni(T obj) {
		return new Omni(obj, this);
	}

	// --------- /Omni Factory --------- //

	public ClassInfo getClassInfo(Class cls) {
		ClassInfo ci = classInfoByClass.get(cls);

		// if null we build it and put it in cache.
		// Note, for performance reason this method is not synchronized,
		// worst case scenario, we create the same ClassInfo twice, but not big deal
		// since classInfoByClass is Concurrent.
		if (ci == null) {
			ci = new ClassInfo(cls);
			classInfoByClass.put(cls, ci);
		}
		return ci;
	}


	// --------- Helpers --------- //
	private <T, R> R convert(ConvertType convertType, TypeConvertor<T, R> typeConvertor, T source, Class<R> targetClass) {
		switch (convertType) {
			case identity:
				return (R) source;
			case none:
				return null; // TODO: later, return default null
			case convertor:
				return typeConvertor.convert(source);
			case complex:
				return as(targetClass, source);
			default:
				throw new RuntimeException("Mapper.convert: no ConvertType for " + source);
		}
	}
	// --------- /Helpers --------- //




}
