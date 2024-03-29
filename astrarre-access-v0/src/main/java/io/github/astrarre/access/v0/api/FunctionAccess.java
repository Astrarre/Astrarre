package io.github.astrarre.access.v0.api;

import java.util.function.Function;

import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.ArrayFunc;
import io.github.astrarre.util.v0.api.func.IterFunc;

public class FunctionAccess<A, B> extends Access<Function<A, B>> {
	public final FunctionAccessHelper<A, Function<A, B>> accessHelper;
	private boolean addedProviderFunction, addedInstanceof;

	/**
	 * combines {@link FunctionAccess (AccessFunction)} and {@link FunctionAccess (BinaryOperator)}
	 */
	public FunctionAccess(Id id) {
		this(id, a -> val -> {
			for (Function<A, B> function : a) {
				B ret = function.apply(val);
				if (ret != null) {
					return ret;
				}
			}
			return null;
		}, a -> null);
	}

	public FunctionAccess(Id id, ArrayFunc<Function<A, B>> iterFunc) {
		this(id, iterFunc, iterFunc.empty());
	}

	public FunctionAccess(Id id, ArrayFunc<Function<A, B>> iterFunc, Function<A, B> empty) {
		super(id, iterFunc);
		this.accessHelper = new FunctionAccessHelper<>(AccessHelpers.ctx(this, f -> a -> f.apply(a).apply(a), empty));
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <A, B> FunctionAccess<A, B> newInstance(Id id, IterFunc<B> combiner) {
		return new FunctionAccess<>(id, functions -> a -> transform(functions, f -> f.apply(a), combiner));
	}

	/**
	 * adds a function for {@link Provider}
	 *
	 * (calling this multiple times will only register it once)
	 */
	public FunctionAccess<A, B> addProviderFunction() {
		if (this.addedProviderFunction) {
			return this;
		}
		this.addedProviderFunction = true;

		this.andThen(a -> {
			if (a instanceof Provider) {
				return (B) ((Provider) a).get(this);
			}
			return null;
		});
		this.accessHelper.forGenericProvider(this);
		return this;
	}

	/**
	 * adds a function for if A instanceof B, return A
	 * @param type the instance
	 */
	public FunctionAccess<A, B> addInstanceOfFunction(TypeToken<B> type) {
		if (this.addedInstanceof) {
			return this;
		}
		this.addedInstanceof = true;
		this.andThen(a -> {
			if (a != null && type.isSupertypeOf(a.getClass())) {
				return (B) a;
			}
			return null;
		});
		return this;
	}

	public FunctionAccess<A, B> addInstanceOfFunction(Class<B> type) {
		return this.addInstanceOfFunction(TypeToken.of(type));
	}

	/**
	 * filters the access function for only objects that are {@code a == b} to the passed object.
	 * This holds a WEAK reference to the object
	 */
	public FunctionAccess<A, B> forInstance(A a, Function<A, B> function) {
		this.accessHelper.forInstanceWeak(a, function);
		return this;
	}

	/**
	 * filters the access function for only objects that are {@link Object#equals(Object)} to the passed object.
	 */
	public FunctionAccess<A, B> forInstanceStrong(A a, Function<A, B> function) {
		this.accessHelper.forInstanceStrong(a, function);
		return this;
	}

	/**
	 * only accepts if the passed value's class is the exact same the passed type. This means this will <b>NOT</b> obey inheritance!
	 *
	 * @param type the type
	 */
	public <C extends A> FunctionAccess<A, B> forClassExact(Class<C> type, Function<C, B> function) {
		this.accessHelper.forClassExact(type, (Function<A, B>) function);
		return this;
	}
}
