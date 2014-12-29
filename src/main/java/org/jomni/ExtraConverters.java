/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.jomni;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * <p></p>
 */
public class ExtraConverters {

	static private DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	static private DateTimeFormatter localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

	static Date localDateTimeToDate(LocalDateTime ldt){
		ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
		return Date.from(zdt.toInstant());
	}

	static LocalDateTime dateToLocalDateTime(Date date){
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	static LocalDateTime stringToLocalDateTime(String str){
		return LocalDateTime.parse(str,localDateTimeFormatter);
	}

	static String localDateTimeToString(LocalDateTime ldt) {
		return localDateTimeFormatter.format(ldt);
	}

	static LocalDate stringToLocalDate(String str){
		return LocalDate.parse(str,localDateFormatter);
	}

	static String localDateToString(LocalDate ld) {
		return localDateFormatter.format(ld);
	}

	static boolean toBoolean(String name, Optional<Object> val) {
		return (boolean) val.map(o -> {
			if ((o instanceof Boolean) || (o.getClass().isAssignableFrom(Boolean.TYPE))) {
				return o;
			} else {
				boolean isAllowedType = o instanceof Character;
				isAllowedType |= o.getClass().isAssignableFrom(Character.TYPE);
				isAllowedType |= o instanceof String;
				isAllowedType |= o instanceof Integer;
				isAllowedType |= o.getClass().isAssignableFrom(Integer.TYPE);
				isAllowedType |= o instanceof Long;
				isAllowedType |= o.getClass().isAssignableFrom(Long.TYPE);

				if (!isAllowedType) {
					throw new IllegalArgumentException("name cannot be converted to boolean as it is a " + val.get().getClass());
				}

				return o.toString().equalsIgnoreCase("1") ||
						o.toString().equalsIgnoreCase("yes") ||
						o.toString().equalsIgnoreCase("true") ||
						o.toString().equalsIgnoreCase("y");

			}
		}).orElse(false);
	}

	public static <T extends Number> T toNumber(Number number, Class<T> target) {
		if (number == null) {
			return null;
		}
		try {
			return target.getConstructor(String.class).newInstance(number.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static BigDecimal toBigDecimal(Number number) {
		return (number == null)? null: new BigDecimal(number.toString());
	}

	public static Boolean toBoolean(String o) {
		if (o == null) {
			return false;
		}

		return o.toString().equalsIgnoreCase("1") ||
				o.toString().equalsIgnoreCase("yes") ||
				o.toString().equalsIgnoreCase("true") ||
				o.toString().equalsIgnoreCase("y");

	}

	public static Boolean toBoolean(Character o) {
		if (o == null) {
			return false;
		}
		return o.charValue() == '1' || o.charValue() == 'y' || o.charValue() == 'Y';
	}

	public static Boolean toBoolean(Number o) {
		if (o == null) {
			return false;
		}
		return o.intValue() == 1;
	}

	//	public static <T extends Number> T toNumber(Number number, Class<T> target) {
	public static <T extends Enum> T toEnum(String val, Class<T> cls){
		return null;
	}


}
