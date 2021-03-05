package io.github.astrarre.recipes.v0.api.recipe;

import java.util.function.Consumer;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.recipe.builder.ZeroBuilder;

public interface Recipe {
	Result applyGeneric(Object... values);

	<V> Iterable<V> getInputs(RecipePart<V, ?> parser, int index);

	void addReloadListener(Consumer<Recipe> listener);

	static ZeroBuilder builder() {
		return ZeroBuilder.builder();
	}

	interface Mono<A> extends Recipe {
		Result apply(A value);
	}

	interface Bi<A, B> extends Recipe {
		Result apply(A a, B b);
	}

	interface Tri<A, B, C> extends Recipe {
		Result apply(A a, B b, C c);
	}

	interface Quad<A, B, C, D> extends Recipe {
		Result apply(A a, B b, C c, D d);
	}

	interface Pent<A, B, C, D, E> extends Recipe {
		Result apply(A a, B b, C c, D d, E e);
	}
}
