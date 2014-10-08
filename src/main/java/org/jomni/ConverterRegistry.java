/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.jomni;

import org.jomni.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.jomni.util.Pair.newPair;

/**
 * Inspired from https://github.com/jexenberger/lambda-tuples/ project:
 * https://github.com/jexenberger/lambda-tuples/blob/master/src/main/java/org/github/lambatuples/TransformerService.java
 */
public class ConverterRegistry {

	private final Map<Pair<Class<?>, Class<?>>, TypeConverter<?, ?>> registry;

	// build a default Transformer
	ConverterRegistry() {
		registry = new HashMap<>();
	}



	void init() {
		addTypeConverter(String.class, Long.class, Long::valueOf);
		addTypeConverter(String.class, Long.TYPE, Long::valueOf);
		addTypeConverter(String.class, Integer.class, Integer::valueOf);
		addTypeConverter(String.class, Integer.TYPE, Integer::valueOf);
		addTypeConverter(String.class, Byte.class, Byte::valueOf);
		addTypeConverter(String.class, Byte.TYPE, Byte::valueOf);
		addTypeConverter(String.class, Short.class, Short::valueOf);
		addTypeConverter(String.class, Short.TYPE, Short::valueOf);
		addTypeConverter(String.class, Boolean.class, ExtraConverters::toBoolean);
		addTypeConverter(String.class, Boolean.TYPE, ExtraConverters::toBoolean);
		addTypeConverter(String.class, Float.class, Float::valueOf);
		addTypeConverter(String.class, Float.TYPE, Float::valueOf);
		addTypeConverter(String.class, Double.class, Double::valueOf);
		addTypeConverter(String.class, Double.TYPE, Double::valueOf);
		addTypeConverter(String.class, Character.class, (instance) -> (instance != null) ? instance.charAt(0) : (char) (byte) 0);
		addTypeConverter(String.class, Character.TYPE, (instance) -> (instance != null) ? instance.charAt(0) : (char) (byte) 0);
		addTypeConverter(String.class, Double.TYPE, Double::valueOf);
		addTypeConverter(String.class, BigDecimal.class, BigDecimal::new);
		addTypeConverter(Number.class, Long.class, (instance) -> ExtraConverters.toNumber(instance, Long.class));
		addTypeConverter(Number.class, Long.TYPE, (instance) -> ExtraConverters.toNumber(instance, Long.class));
		addTypeConverter(Number.class, Integer.class, (instance) -> ExtraConverters.toNumber(instance, Integer.class));
		addTypeConverter(Number.class, Integer.TYPE, (instance) -> ExtraConverters.toNumber(instance, Integer.class));
		addTypeConverter(Number.class, Byte.class, (instance) -> ExtraConverters.toNumber(instance, Byte.class));
		addTypeConverter(Number.class, Byte.TYPE, (instance) -> ExtraConverters.toNumber(instance, Byte.class));
		addTypeConverter(Number.class, Short.class, (instance) -> ExtraConverters.toNumber(instance, Short.class));
		addTypeConverter(Number.class, Short.TYPE, (instance) -> ExtraConverters.toNumber(instance, Short.class));
		addTypeConverter(Number.class, Boolean.class, ExtraConverters::toBoolean);
		addTypeConverter(Number.class, Boolean.TYPE, ExtraConverters::toBoolean);
		addTypeConverter(Number.class, Float.class, (instance) -> ExtraConverters.toNumber(instance, Float.class));
		addTypeConverter(Number.class, Float.TYPE, (instance) -> ExtraConverters.toNumber(instance, Float.class));
		addTypeConverter(Number.class, Double.class, (instance) -> ExtraConverters.toNumber(instance, Double.class));
		addTypeConverter(Number.class, Double.TYPE, (instance) -> ExtraConverters.toNumber(instance, Double.class));
		addTypeConverter(Number.class, BigDecimal.class, ExtraConverters::toBigDecimal);
		addTypeConverter(Object.class, String.class, Object::toString);
		addTypeConverter(Character.class, Boolean.class, ExtraConverters::toBoolean);
		addTypeConverter(Number.class, Boolean.TYPE, ExtraConverters::toBoolean);
		addTypeConverter(LocalDateTime.class, LocalDate.class, (LocalDateTime instance) -> instance.toLocalDate());
		addTypeConverter(LocalDate.class, LocalDateTime.class, (LocalDate instance) -> instance.atStartOfDay());
		addTypeConverter(LocalDateTime.class, Date.class, ExtraConverters::localDateTimeToDate);
		addTypeConverter(Date.class, LocalDateTime.class, ExtraConverters::dateToLocalDateTime);
	}

	<T, R> void addTypeConverter(Class<T> source, Class<R> target, TypeConverter<T, R> typeConverter) {
		registry.put(newPair(source, target), typeConverter);
	}

	Map<Pair<Class<?>, Class<?>>, TypeConverter<?, ?>> getRegistry() {
		return registry;
	}

	// --------- /initialization --------- //

	// --------- Converter Helpers --------- //

	TypeConverter resolveTypeConverter(Class<?> source, Class<?> target) {

		TypeConverter<?, ?> typeConverter = findWideningTypeConverter(source, target);

		if (typeConverter == null && target.isEnum()){
			// special converter for enum
			if (target.isEnum()) {
				return (value) -> {
					Class enumTarget = (Class<Enum>) target;
					try {
						return Enum.valueOf(enumTarget, value.toString());
					} catch (Throwable e) {
						throw e;
					}
				};
			}
		}
		if (typeConverter == null) {
			// if is it is a primitive or value isNull, then, return a lamdda converter
			if (target.isPrimitive()){ // || isNull){
				return (instance) -> {
					if (target.isPrimitive()) {
						try {
							return target.getField("TYPE").get(null);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					} else {
						return null;
					}
				};
			}
			// NOTE: if no primitive or isNull, then, here typeConverter will be null
		}
		return typeConverter;
	}

	private TypeConverter<?, ?> findWideningTypeConverter(Class<?> source, Class<?> target) {

		TypeConverter<?, ?> typeConverter = registry.get(newPair(source, target));
		if (typeConverter != null) {
			return typeConverter;
		}

		//do a widening search
		Pair<Class<?>, Class<?>> transformerKey = registry
				.keySet()
				.stream()
				.filter(key -> {
					boolean sourceAssignable = key.getA().isAssignableFrom(source);
					boolean targetAssignable = key.getB().isAssignableFrom(target);
					return sourceAssignable && targetAssignable;
				})
				.findFirst().orElse(null);
		return registry.get(transformerKey);
	}
	// --------- /Converter Helpers --------- //


}