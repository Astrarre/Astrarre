package io.github.astrarre.util.v0.api.func;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import io.github.astrarre.util.internal.factory.AbstractLoopArrayMethodFactory;
import io.github.astrarre.util.internal.factory.ConditionLoopArrayMethodFactory;
import io.github.astrarre.util.internal.factory.ProxyClassFactory;
import io.github.astrarre.util.internal.factory.RunLoopArrayMethodFactory;
import io.github.astrarre.util.v0.api.Validate;
import io.github.astrarre.util.v0.api.func.predicate.BoolPredicate;
import io.github.astrarre.util.v0.api.func.predicate.BytePredicate;
import io.github.astrarre.util.v0.api.func.predicate.CharPredicate;
import io.github.astrarre.util.v0.api.func.predicate.FloatPredicate;
import io.github.astrarre.util.v0.api.func.predicate.ShortPredicate;
import org.objectweb.asm.Type;

@SuppressWarnings({
		"unchecked",
		"UnstableApiUsage"
})
public interface ArrayFunc<A> extends Function<A[], A>, Serializable {
	/**
	 * finds the first non-null value in the list, otherwise returns null
	 */
	ArrayFunc NON_NULL = (ArrayFunc<?>) arr -> {
		for(Object a : arr) {
			if(a != null) {
				return a;
			}
		}
		return null;
	};

	static <A> IterFunc<A> iter(ArrayFunc<A> func) {
		return func.asIter();
	}

	@Override
	default A apply(A[] as) {
		return this.combine(as);
	}

	default A empty() {
		return this.combine((A[]) Array.newInstance(this.getType(), 0));
	}

	A combine(A[] array);

	default IterFunc<A> asIter(Class<A> type) {
		return arr -> this.combine(Iterables.toArray(arr, type));
	}

	default Class<A> getType() {
		// egregious type hacks
		TypeToken<?> token = new TypeToken<A>(this.getClass()) {};
		Class<?> type = token.getRawType();
		if(type == Object.class) { // doesn't work on lambda
			try {
				Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
				writeReplace.setAccessible(true);
				SerializedLambda sl = (SerializedLambda) writeReplace.invoke(this);
				type = Class.forName(Type.getMethodType(sl.getInstantiatedMethodType()).getReturnType().getClassName());
			} catch(ReflectiveOperationException e) {
			}
		}
		return (Class<A>) type;
	}

	default IterFunc<A> asIter() {
		return this.asIter(this.getType());
	}

	static Builder builder() {
		return new Builder();
	}

	static <F> HandledBuilder<F> builder(Class<F> type, F empty) {
		return new HandledBuilder<>(new Builder(), type, empty);
	}

	class Builder {
		final Map<String, Object> defaultValues = new HashMap<>();
		final List<Function<Class<?>, AbstractLoopArrayMethodFactory>> factories = new ArrayList<>();
		int counter = 0;

		public Builder() {
		}

		public Builder voidMethod(FuncFinder finder) {
			this.factories.add(c -> new RunLoopArrayMethodFactory(finder.find(c)));
			return this;
		}

		/**
		 * return if not null
		 */
		public Builder retIfNN(FuncFinder finder) {
			return this.returnIf(finder, Objects::nonNull, null);
		}

		public Builder returnIf(FuncFinder finder, BoolPredicate predicate, boolean val) {
			return this.returnIf(finder, predicate, boolean.class, val, false);
		}

		public Builder returnIf(FuncFinder finder, BytePredicate predicate, byte val) {
			return this.returnIf(finder, predicate, byte.class, val, false);
		}

		public Builder returnIf(FuncFinder finder, CharPredicate predicate, char val) {
			return this.returnIf(finder, predicate, char.class, val, false);
		}

		public Builder returnIf(FuncFinder finder, ShortPredicate predicate, short val) {
			return this.returnIf(finder, predicate, short.class, val, false);
		}

		public Builder returnIf(FuncFinder finder, IntPredicate predicate, int val) {
			return this.returnIf(finder, predicate, int.class, val, false);
		}

		public Builder returnIf(FuncFinder finder, FloatPredicate predicate, float val) {
			return this.returnIf(finder, predicate, float.class, val, false);
		}

		public Builder returnIf(FuncFinder finder, DoublePredicate predicate, double val) {
			return this.returnIf(finder, predicate, double.class, val, false);
		}

		public Builder returnIf(FuncFinder finder, LongPredicate predicate, long val) {
			return this.returnIf(finder, predicate, long.class, val, false);
		}

		public <R> Builder returnIf(FuncFinder finder, Predicate<R> predicate, R val) {
			return this.returnIf(finder, predicate, Object.class, val, false);
		}

		public <F> ArrayFunc<F> build(Class<F> type) {
			var factory = new ProxyClassFactory<>(type);
			for(var function : this.factories) {
				factory.add(function.apply(type));
			}
			return new ArrayFunc<>() {
				@Override
				public F combine(F[] array) {
					Map<String, Object> values = new HashMap<>(Builder.this.defaultValues);
					values.put(AbstractLoopArrayMethodFactory.ARRAY_FIELD_NAME, array);
					return factory.init(values);
				}

				@Override
				public Class<F> getType() {
					return type;
				}
			};
		}

		public <F> ArrayFunc<F> buildInfer(F... infer) {
			return this.build((Class<F>) infer.getClass().componentType());
		}

		public <F> HandledBuilder<F> withEmpty(Class<F> type, F empty) {
			return new HandledBuilder<>(this, type, empty);
		}

		public <F> HandledBuilder<F> inferEmpty(F empty, F... infer) {
			return new HandledBuilder<>(this, (Class<F>)infer.getClass().componentType(), empty);
		}

		private Builder returnIf(FuncFinder finder, Object predicate, Class<?> returnType, Object defaultValue, boolean function) {
			String name = "default" + Integer.toHexString(this.counter++);
			this.factories.add(c -> {
				Method method = finder.find(c);
				if(returnType != Object.class || method.getReturnType().isPrimitive()) {
					Validate.equals((expected, value) -> "Expected " + expected + " return type, got " + value, returnType, method.getReturnType());
				}
				return new ConditionLoopArrayMethodFactory(method, function ? "nullHandler" : name, name + "Predicate", function);
			});
			this.defaultValues.put(name + "Predicate", predicate);
			this.defaultValues.put(function ? "nullHandler" : name, defaultValue);
			return this;
		}
	}

	record HandledBuilder<F>(Builder builder, Class<F> cls, F empty) {
		public HandledBuilder<F> returnIf(FuncFinder finder, BoolPredicate predicate) {
			return this.returnIf(finder, predicate, boolean.class);
		}

		public HandledBuilder<F> returnIf(FuncFinder finder, BytePredicate predicate) {
			return this.returnIf(finder, predicate, byte.class);
		}

		public HandledBuilder<F> returnIf(FuncFinder finder, CharPredicate predicate) {
			return this.returnIf(finder, predicate, char.class);
		}

		public HandledBuilder<F> returnIf(FuncFinder finder, ShortPredicate predicate) {
			return this.returnIf(finder, predicate, short.class);
		}

		public HandledBuilder<F> returnIf(FuncFinder finder, IntPredicate predicate) {
			return this.returnIf(finder, predicate, int.class);
		}

		public HandledBuilder<F> returnIf(FuncFinder finder, FloatPredicate predicate) {
			return this.returnIf(finder, predicate, float.class);
		}

		public HandledBuilder<F> returnIf(FuncFinder finder, DoublePredicate predicate) {
			return this.returnIf(finder, predicate, double.class);
		}

		public HandledBuilder<F> returnIf(FuncFinder finder, LongPredicate predicate) {
			return this.returnIf(finder, predicate, long.class);
		}

		public <R> HandledBuilder<F> returnIf(FuncFinder finder, Predicate<R> predicate) {
			return this.returnIf(finder, predicate, Object.class);
		}

		public ArrayFunc<F> build() {
			return this.builder.build(this.cls);
		}

		private HandledBuilder<F> returnIf(FuncFinder finder, Object predicate, Class<?> returnType) {
			this.builder.returnIf(finder, predicate, returnType, this.empty, true);
			return this;
		}
	}
}
