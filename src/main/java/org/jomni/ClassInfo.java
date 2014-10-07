/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.jomni;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static java.lang.String.format;

/**
 * Created by jeremychone on 3/9/14.
 */
public class ClassInfo {

	private static Set<String> excludePropNames = new HashSet<>(Arrays.asList(new String[] {"class"}));
	private final Class objectClass;

	Map<String, PropInfo> propInfoByPropName = new HashMap<>();

	public ClassInfo(Class objectClass) {
		this.objectClass = objectClass;
		try{
			BeanInfo bi = Introspector.getBeanInfo(objectClass);
			PropertyDescriptor[] propertyDescriptors = bi.getPropertyDescriptors();

			for (PropertyDescriptor pd : propertyDescriptors){
				Method readMethod = pd.getReadMethod();
				if (readMethod != null && !excludePropNames.contains(pd.getName())){
					// get name and type.
					String propName = pd.getName();
					Class propType = pd.getPropertyType();

					// get the readMethod eventual genericType
					Class propGenericType = null;
					Type returnType = readMethod.getGenericReturnType();
					if(returnType instanceof ParameterizedType){
						ParameterizedType type = (ParameterizedType) returnType;
						Type[] typeArguments = type.getActualTypeArguments();
						// TODO: should probably get the first only
						for(Type typeArgument : typeArguments){
							propGenericType = (Class) typeArgument;
						}
					}



					// get the writeMethod
					Method writeMethod = pd.getWriteMethod();
					// if the writeMethod is null, probably because it is a chainable one
					if (writeMethod == null){
						String readMethodName = readMethod.getName();
						// TODO: will need to support the "is..." readerMethod as well.
						if (readMethodName.startsWith("get")){
							String writeMethodName = "set" + readMethodName.substring(3);
							try{
								writeMethod = objectClass.getMethod(writeMethodName,propType);
							}catch (NoSuchMethodException nsme){
								// fine, no setter for this one.
							}
						}
					}

					PropInfo propInfo = new PropInfo(propName, propType, propGenericType, writeMethod, readMethod);
					propInfoByPropName.put(propName,propInfo);
				}
			}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the ObjectClass of this ClassInfo
	 */
	public Class getObjectClass(){
		return objectClass;
	}

	public Object getValue(Object obj,String propName) {
		Object val = null;
		PropInfo propInfo = propInfoByPropName.get(propName);
		if (propInfo != null){
			Method readMethod = propInfo.getReadMethod();
			if (readMethod == null){
				throw new RuntimeException(format("propName %s does not not have readMethod",propName));
			}
			try {
				val = readMethod.invoke(obj);
			} catch (Throwable e) {
				throw new RuntimeException(format("Error while calling read method %s for property %s on object %s", readMethod.getName(), propName, obj) + "\n" + e,e);
			}
		}
		return val;
	}

	/**
	 * Set the value of an object given the properties name. Note that no occurs transformation  at this point. Type must match.
	 * @param obj
	 * @param propName
	 * @param value
	 * @return
	 */
	public boolean setValue(Object obj, String propName, Object value){
		boolean done = false;
		PropInfo propInfo = propInfoByPropName.get(propName);
		Method writeMethod = (propInfo != null)?propInfo.getWriteMethod():null;
		if (writeMethod != null){
			try {
				if (value == null){
					writeMethod.invoke(obj,new Object[]{ null });
					done = true;
				}else{
					// if value is null, it means the transformer fail to convert it.
					// TODO: perhaps to use strict transformer.
					if (value != null){
						writeMethod.invoke(obj,value);
						done = true;
					}
				}
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}

		}
		return done;
	}

	public Set<String> getPropertyNames(){
		return propInfoByPropName.keySet();
	}

	public PropInfo getPropInfo(String name) {
		return propInfoByPropName.get(name);
	}

	public boolean hasProperty(String name){
		return propInfoByPropName.containsKey(name);
	}

}

