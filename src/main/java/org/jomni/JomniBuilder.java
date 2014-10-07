package org.jomni;

public class JomniBuilder {

	ConvertorRegistry convertorsOverride = new ConvertorRegistry();

	public JomniMapper build(){
		return new JomniMapper(convertorsOverride);
	}

	public <T, R> void addTypeConvertor(Class<T> source, Class<R> target, TypeConvertor<T, R> typeConvertor) {
		convertorsOverride.registerTypeConvertor(source, target, typeConvertor);
	}

}
