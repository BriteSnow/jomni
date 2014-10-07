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
 * 
 */
public class ConvertorRegistry {

	private final Map<Pair<Class<?>, Class<?>>, TypeConvertor<?, ?>> registry;

	// build a default Transformer
	ConvertorRegistry() {
		registry = new HashMap<>();
	}



	void init() {
		registerTypeConvertor(String.class, Long.class, Long::valueOf);
		registerTypeConvertor(String.class, Long.TYPE, Long::valueOf);
		registerTypeConvertor(String.class, Integer.class, Integer::valueOf);
		registerTypeConvertor(String.class, Integer.TYPE, Integer::valueOf);
		registerTypeConvertor(String.class, Byte.class, Byte::valueOf);
		registerTypeConvertor(String.class, Byte.TYPE, Byte::valueOf);
		registerTypeConvertor(String.class, Short.class, Short::valueOf);
		registerTypeConvertor(String.class, Short.TYPE, Short::valueOf);
		registerTypeConvertor(String.class, Boolean.class, ExtraConvertors::toBoolean);
		registerTypeConvertor(String.class, Boolean.TYPE, ExtraConvertors::toBoolean);
		registerTypeConvertor(String.class, Float.class, Float::valueOf);
		registerTypeConvertor(String.class, Float.TYPE, Float::valueOf);
		registerTypeConvertor(String.class, Double.class, Double::valueOf);
		registerTypeConvertor(String.class, Double.TYPE, Double::valueOf);
		registerTypeConvertor(String.class, Character.class, (instance) -> (instance != null) ? instance.charAt(0) : (char) (byte) 0);
		registerTypeConvertor(String.class, Character.TYPE, (instance) -> (instance != null) ? instance.charAt(0) : (char) (byte) 0);
		registerTypeConvertor(String.class, Double.TYPE, Double::valueOf);
		registerTypeConvertor(String.class, BigDecimal.class, BigDecimal::new);
		registerTypeConvertor(Number.class, Long.class, (instance) -> ExtraConvertors.toNumber(instance, Long.class));
		registerTypeConvertor(Number.class, Long.TYPE, (instance) -> ExtraConvertors.toNumber(instance, Long.class));
		registerTypeConvertor(Number.class, Integer.class, (instance) -> ExtraConvertors.toNumber(instance, Integer.class));
		registerTypeConvertor(Number.class, Integer.TYPE, (instance) -> ExtraConvertors.toNumber(instance, Integer.class));
		registerTypeConvertor(Number.class, Byte.class, (instance) -> ExtraConvertors.toNumber(instance, Byte.class));
		registerTypeConvertor(Number.class, Byte.TYPE, (instance) -> ExtraConvertors.toNumber(instance, Byte.class));
		registerTypeConvertor(Number.class, Short.class, (instance) -> ExtraConvertors.toNumber(instance, Short.class));
		registerTypeConvertor(Number.class, Short.TYPE, (instance) -> ExtraConvertors.toNumber(instance, Short.class));
		registerTypeConvertor(Number.class, Boolean.class, ExtraConvertors::toBoolean);
		registerTypeConvertor(Number.class, Boolean.TYPE, ExtraConvertors::toBoolean);
		registerTypeConvertor(Number.class, Float.class, (instance) -> ExtraConvertors.toNumber(instance, Float.class));
		registerTypeConvertor(Number.class, Float.TYPE, (instance) -> ExtraConvertors.toNumber(instance, Float.class));
		registerTypeConvertor(Number.class, Double.class, (instance) -> ExtraConvertors.toNumber(instance, Double.class));
		registerTypeConvertor(Number.class, Double.TYPE, (instance) -> ExtraConvertors.toNumber(instance, Double.class));
		registerTypeConvertor(Number.class, BigDecimal.class, ExtraConvertors::toBigDecimal);
		registerTypeConvertor(Object.class, String.class, Object::toString);
		registerTypeConvertor(Character.class, Boolean.class, ExtraConvertors::toBoolean);
		registerTypeConvertor(Number.class, Boolean.TYPE, ExtraConvertors::toBoolean);
		registerTypeConvertor(LocalDateTime.class, LocalDate.class, (LocalDateTime instance) -> instance.toLocalDate());
		registerTypeConvertor(LocalDate.class, LocalDateTime.class, (LocalDate instance) -> instance.atStartOfDay());
		registerTypeConvertor(LocalDateTime.class, Date.class, ExtraConvertors::localDateTimeToDate);
		registerTypeConvertor(Date.class, LocalDateTime.class, ExtraConvertors::dateToLocalDateTime);
	}

	<T, R> void registerTypeConvertor(Class<T> source, Class<R> target, TypeConvertor<T, R> typeConvertor) {
		registry.put(newPair(source, target), typeConvertor);
	}

	Map<Pair<Class<?>, Class<?>>, TypeConvertor<?, ?>> getRegistry() {
		return registry;
	}

	// --------- /initialization --------- //

	// --------- Convertor Helpers --------- //

	TypeConvertor resolveConvertor(Class<?> source, Class<?> target) {

		TypeConvertor<?, ?> typeConvertor = findWideningTransformer(source, target);

		if (typeConvertor == null && target.isEnum()){
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
		if (typeConvertor == null) {
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
			// NOTE: if no primitive or isNull, then, here typeConvertor will be null
		}
		return typeConvertor;
	}

	private TypeConvertor<?, ?> findWideningTransformer(Class<?> source, Class<?> target) {

		TypeConvertor<?, ?> typeConvertor = registry.get(newPair(source, target));
		if (typeConvertor != null) {
			return typeConvertor;
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
	// --------- /Convertor Helpers --------- //


}