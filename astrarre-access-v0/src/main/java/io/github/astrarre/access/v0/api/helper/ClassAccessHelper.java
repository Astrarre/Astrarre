package io.github.astrarre.access.v0.api.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.internal.TypeHelper;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.util.v0.api.func.IterFunc;

public class ClassAccessHelper<T, F> extends AbstractClassAccessHelper<T, Class<? extends T>, F> {


	public ClassAccessHelper(AccessHelpers.Context<Class<? extends T>, F> context) {
		super(context);
	}

	@Override
	protected Class<? extends T> convert(Type type) {
		return (Class<? extends T>) TypeHelper.raw(type);
	}

	/**
	 * resolves the generics of the class and filters accordingly, remember that type erasure exists, so this wont be able to filter {@code
	 * Map<String, Object>} unless there is a subclass of it that implements {@code Map<String, Object>}
	 */
	@Override
	public ClassAccessHelper<T, F> forTypeGeneric(TypeToken<? extends T> token, F func) {
		return (ClassAccessHelper<T, F>) super.forTypeGeneric(token, func);
	}

	/**
	 * resolves the generics of the class and filters accordingly, remember that type erasure exists, so this wont be able to filter {@code
	 * Map<String, Object>} unless there is a subclass of it that implements {@code Map<String, Object>}
	 */
	@Override
	public ClassAccessHelper<T, F> forTypeGeneric(ParameterizedType type, F func) {
		return (ClassAccessHelper<T, F>) super.forTypeGeneric(type, func);
	}
}
