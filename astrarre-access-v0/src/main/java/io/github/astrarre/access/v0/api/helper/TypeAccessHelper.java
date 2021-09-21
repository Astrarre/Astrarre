package io.github.astrarre.access.v0.api.helper;

import java.lang.reflect.Type;

public class TypeAccessHelper<T, F> extends AbstractClassAccessHelper<T, Type, F> {

	public TypeAccessHelper(AccessHelpers.Context<Type, F> context) {
		super(context);
	}

	@Override
	protected Type convert(Type type) {
		return type;
	}
}
