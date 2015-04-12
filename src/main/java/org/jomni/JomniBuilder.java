package org.jomni;


/**
 * JomniMapper builder.
 *
 * This Builder is designed to be used in a single thread and return a immutable JomniMapper (which can be used in multiple thread)
 */
public class JomniBuilder {

	ConverterRegistry convertersOverride = new ConverterRegistry();

	public JomniMapper build(){
		return new JomniMapper(convertersOverride);
	}

	public <T, R> void addTypeConverter(Class<T> source, Class<R> target, TypeConverter<T, R> typeConverter) {
		convertersOverride.addTypeConverter(source, target, typeConverter);
	}

}
