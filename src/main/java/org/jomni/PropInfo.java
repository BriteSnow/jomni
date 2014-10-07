/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.jomni;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Created by jeremychone on 3/9/14.
 */
public class PropInfo {

	private final String name;
	private final Method readMethod;
	private final Method writeMethod;
	private final Class type;
	private final Optional<Class> genericType;


	public PropInfo(String name, Class type, Class genericType, Method writeMethod, Method readMethod) {
		this.name = name;
		this.type = type;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.genericType = Optional.ofNullable(genericType);
	}

	public String getName() {
		return name;
	}

	public Method getReadMethod() {
		return readMethod;
	}

	public Method getWriteMethod() {
		return writeMethod;
	}

	public Class getType() {
		return type;
	}

	public Optional<Class> getGenericType() {
		return genericType;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[").append(type.getSimpleName());
		genericType.ifPresent((gen) -> sb.append("<").append(gen.getSimpleName()).append(">"));
		return sb.append(" ").append(name).append(" ").append(readMethod.getName()).append(" ").append(writeMethod.getName()).append("]").toString();
	}

}
