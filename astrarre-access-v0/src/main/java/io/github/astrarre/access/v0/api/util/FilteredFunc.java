package io.github.astrarre.access.v0.api.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.internal.factory.ContextExtractingMethodBuilder;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.util.internal.factory.ProxyClassFactory;
import io.github.astrarre.util.v0.api.func.FuncFinder;
import io.github.astrarre.util.v0.api.func.IterFunc;

/**
 * creates a function that conditionally executes a function based on some context
 */
public interface FilteredFunc<I, F> extends Function<Function<I, F>, F> {
	/**
	 * To use this, you implement your interface, and for the context you want to filter for, pass it into the getter,
	 * this should give you the function(s) that matched the given filter, which u then execute with the given context
	 */
	@Override
	F apply(Function<I, F> functionGetter);

	default AddingImpl<I, F> then(Access<F> access) {
		return new AddingImpl<>(access, Access::andThen, this);
	}

	default AddingImpl<I, F> before(Access<F> access) {
		return new AddingImpl<>(access, Access::before, this);
	}

	interface Adding<I, F> extends Consumer<Function<I, F>> {
	}

	record AddingImpl<I, F>(F empty, IterFunc<F> func, Access<F> access, BiConsumer<Access<F>, F> op, FilteredFunc<I, F> filter) implements Adding<I, F>, AccessHelpers.Context<I, F> {
		public AddingImpl(Access<F> access, BiConsumer<Access<F>, F> op, FilteredFunc<I, F> filter) {
			this(access.combiner.empty(), access.combiner, access, op, filter);
		}

		@Override
		public void accept(Function<I, F> function) {
			this.op.accept(this.access, this.filter.apply(function));
		}

		@Override
		public Adding<I, F> andThen() {
			return this;
		}

		public AddingImpl<I, F> withIter(IterFunc<F> comb) {
			return new AddingImpl<>(comb.empty(), comb, this.access, this.op, this.filter);
		}

		public AddingImpl<I, F> withEmpty(F empty) {
			return new AddingImpl<>(empty, this.func, this.access, this.op, this.filter);
		}
	}

	static <I> Builder<I> builder(Class<I> type) {
		return new Builder<>(type);
	}

	@SuppressWarnings("unchecked")
	class Builder<I> {
		final Class<I> type;
		List<Context<?, I>> contexts = new ArrayList<>();

		public Builder(Class<I> type) {
			this.type = type;
		}


		record Context<T, I>(FuncFinder finder, Class<T> paramType, int ordinal, Function<T, I> extracter) {}

		/**
		 * Create a context extracter for a given parameter
		 */
		public <T> Builder<I> ordinal(FuncFinder finder, Class<T> parameterType, int ordinal, Function<T, I> extracter) {
			this.contexts.add(new Context<>(finder, parameterType, ordinal, extracter));
			return this;
		}

		public <T> Builder<I> first(FuncFinder finder, Class<T> parameterType, Function<T, I> extracter) {
			this.contexts.add(new Context<>(finder, parameterType, 0, extracter));
			return this;
		}

		public Builder<I> ordinal(FuncFinder finder, int ordinal) {
			this.ordinal(finder, this.type, ordinal, Function.identity());
			return this;
		}

		public Builder<I> first(FuncFinder finder) {
			this.ordinal(finder, this.type, 0, Function.identity());
			return this;
		}

		public Builder<I> first(String methodName) {
			return this.first(FuncFinder.byName(methodName));
		}

		public <F> FilteredFunc<I, F> buildInfer(F defaultValue, F... arr) {
			return this.build((Class<F>)arr.getClass().componentType(), defaultValue);
		}

		public <F> FilteredFunc<I, F> build(Class<F> type, F defaultValue) {
			ProxyClassFactory<F> factory = new ProxyClassFactory<>(type);
			int counter = 0;

			Map<String, Function<Object, I>> extracters = new HashMap<>();
			for(var context : this.contexts) {
				String field = "extracter" + Integer.toHexString(counter);
				Method method = context.finder.find(type);
				extracters.put(field, (Function<Object, I>) context.extracter);
				factory.add(new ContextExtractingMethodBuilder(field, "nullFunction", method, context.paramType, context.ordinal));
			}
			return function -> {
				Map<String, Object> params = new HashMap<>();
				params.put("nullFunction", defaultValue);
				extracters.forEach((s, f) -> params.put(s, (Function<?, F>) i -> function.apply(f.apply(i))));
				return factory.init(params);
			};
		}
	}
}
