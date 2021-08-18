package io.github.astrarre.access.internal;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;


public class TypeHelper {
	public static Class<?> raw(Type type) {
		if(type instanceof Class c) {
			return c;
		} else if(type instanceof GenericArrayType a) {
			return raw(a.getGenericComponentType()).arrayType();
		} else if(type instanceof ParameterizedType t) {
			return raw(t.getRawType());
		} else if(type instanceof TypeVariable v) {
			return raw(v.getBounds()[0]);
		} else if(type instanceof WildcardType t) {
			return raw(t.getUpperBounds()[0]);
		} else if(type == null) {
			return null;
		} else {
			throw new UnsupportedOperationException("Unknown type " + type.getClass());
		}
	}
}
