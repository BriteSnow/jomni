package org.jomni;

public class JomniBuilder {

	ConverterRegistry convertersOverride = new ConverterRegistry();

	public JomniMapper build(){
		return new JomniMapper(convertersOverride);
	}

	public <T, R> void addTypeConverter(Class<T> source, Class<R> target, TypeConverter<T, R> typeConverter) {
		convertersOverride.addTypeConverter(source, target, typeConverter);
	}

}
