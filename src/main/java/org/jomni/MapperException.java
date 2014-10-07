package org.jomni;

import java.util.Objects;

public class MapperException extends RuntimeException {

	public enum Error{
		CANNOT_FIND_RESOLVER;
	}

	private final Error error;


	private MapperException(Error error, String message){
		super(message);
		this.error = error;
	}

	public Error getError(){
		return error;
	}

	public boolean isError(Error error) {
		return Objects.equals(error, this.error);
	}

	static public MapperException noResolverFoundFor(Class sourceClass, Class targetClass) {
		String msg = String.format("Cannot find resolver for sourceClass %s to targetClass %s", sourceClass.getName(),targetClass.getName());
		return new MapperException(Error.CANNOT_FIND_RESOLVER, msg);
	}

}
