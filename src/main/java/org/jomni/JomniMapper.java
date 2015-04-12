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
 */
public class JomniMapper {
	static private final Map<Class,Class> implByInterface = mapOf(Map.class,HashMap.class,List.class,ArrayList.class);

	private enum ConvertType {
		identity, none, converter, complex;
	}

	private Map<Class, ClassInfo> classInfoByClass = new ConcurrentHashMap<>(16, 0.9f, 1);

	private ConverterRegistry converterRegistry = new ConverterRegistry();


	/**
	 * Packaged scoped constructor to force use of Builder.
	 */
	JomniMapper(ConverterRegistry override){
		converterRegistry.init();
		converterRegistry.getRegistry().putAll(override.getRegistry());
	}

	// --------- Public APIs --------- //
	/**
	 * Transform a value to a class. This can be used for complex (i.e pojo) or simple types.
	 *
	 * @param targetClass
	 * @param value
	 * @return
	 */

	public <R, T> R as(Class<R> targetClass, T value) {
		if (value == null){
			return (R) null;
		}

		// if targetClass is an interface, try to find the implClass (if not, let it fail later)
		if (targetClass.isInterface()) {
			Class implClass = implByInterface.get(targetClass);
			targetClass = (implClass != null)?implClass:null;
		}

		Pair<ConvertType, TypeConverter<T, R>> converterInfo = getConvertInfo(value, targetClass);
		JomniMapper.ConvertType converterType = converterInfo.getA();
		TypeConverter<T, R> typeConverter = converterInfo.getB();
		try {
			if (converterType == ConvertType.complex) {
				R targetObject = targetClass.newInstance();
				Omni jSource = omni(value);
				Omni<R> jTarget = omni(targetObject);
				return jTarget.setAll(jSource).get();
			} else {
				return convert(converterType, typeConverter, value, targetClass);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <p>Return a Function<T,R> that will convert a value to a given type.</p>
	 *
	 * <p>Used in the Optional and Stream. For example.  stream.map(jomniMapper.as(User.class)).</p>
	 *
	 *
	 * @param targetClass
	 * @param <T>
	 * @param <R>
	 * @return
	 */
	// TODO: Need to find a way to do something like "Map m = optional.map(j.as(HashMap.class))" right now, types missmatch.
	// TODO: Function<? super T,? extends R>, still does not work (hopefully, missing something "obvious").
	//       public <T,R> Function<? super T,? extends R> as(Class<R> targetClass){
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


	// --------- /Public APIs --------- //

	// --------- Public Omni Factory --------- //
	public <T> Omni<T> as(Supplier<T> supplier) {
		T obj = supplier.get();
		return omni(obj);
	}

	public <T> Omni<T> omni(T obj) {
		return new Omni(obj, this);
	}
	// --------- /Public Omni Factory --------- //



	private <T, R> Pair<ConvertType, TypeConverter<T, R>> getConvertInfo(T instance, Class<R> targetClass) {
		boolean isNull = (instance == null);

		if (instance != null && (targetClass.equals(instance.getClass()) || targetClass.isAssignableFrom(instance.getClass()))) {
			return new Pair(ConvertType.identity, null);
		}

		Class sourceClass = (isNull) ? null : instance.getClass();
		if (!isNull) {
			TypeConverter typeConverter = converterRegistry.resolveTypeConverter(sourceClass, targetClass);
			if (typeConverter != null) {
				return new Pair(ConvertType.converter, typeConverter);
			}
		}

		// now, if no type converters and if instance is null, then, the none convert type will be applied
		if (instance == null) {
			return new Pair(ConvertType.none, null);
		}

		return new Pair(ConvertType.complex, null);
	}



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
	private <T, R> R convert(ConvertType convertType, TypeConverter<T, R> typeConverter, T source, Class<R> targetClass) {
		switch (convertType) {
			case identity:
				return (R) source;
			case none:
				return null; // TODO: later, return default null
			case converter:
				return typeConverter.convert(source);
			case complex:
				return as(targetClass, source);
			default:
				throw new RuntimeException("Mapper.convert: no ConvertType for " + source);
		}
	}
	// --------- /Helpers --------- //




}
