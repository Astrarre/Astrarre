package io.github.astrarre.recipies.v0.api.recipe;

import java.util.List;

import com.google.common.collect.ImmutableList;
import io.github.astrarre.recipies.internal.RecipeImpl;
import io.github.astrarre.recipies.v0.api.part.RecipePart;

public interface Recipe<O extends Output> {
	RecipePart<Double, Output> a = null;
	Recipe<Output.Mono<Double>> TEST = outputs(a).inputs(a);

	O apply(Object... values);

	static <A> InputBuilder<Output.Mono<A>> outputs(RecipePart<A, ?> part) {
		return new InputBuilder<>(ImmutableList.of(part));
	}

	class InputBuilder<O extends Output> {
		private final List<RecipePart<?, ?>> outputs;

		private InputBuilder(List<RecipePart<?, ?>> outputs) {
			this.outputs = outputs;
		}

		public <A> Recipe.Mono<A, O> inputs(RecipePart<A, ?> part) {
			return (Mono<A, O>) new RecipeImpl(this.outputs, ImmutableList.of(part));
		}
	}

	interface Mono<A, O extends Output> extends Recipe<O> {
		O apply(A value);
	}

	interface Bi<A, B, O extends Output> extends Recipe<O> {
		O apply(A a, B b);
	}

	interface Tri<A, B, C, O extends Output> extends Recipe<O> {
		O apply(A a, B b, C c);
	}

	interface Quad<A, B, C, D, O extends Output> extends Recipe<O> {
		O apply(A a, B b, C c, D d);
	}

	interface Pent<A, B, C, D, E, O extends Output> extends Recipe<O> {
		O apply(A a, B b, C c, D d, E e);
	}
}
