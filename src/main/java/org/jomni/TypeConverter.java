/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.jomni;

/**
 * Converter which needs to be define for a given targetType.
 *
 * TODO: We might add a ValueConverter where the targetType will be passed as an argument
 * 		 allowing doing on demand type logic (would be good for Enum which is handled as a specific case)
 */
@FunctionalInterface
public interface TypeConverter<T, R> {

	R convert(T instance);

}