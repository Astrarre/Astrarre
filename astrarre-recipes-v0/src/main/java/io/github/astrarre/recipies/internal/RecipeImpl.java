package io.github.astrarre.recipies.internal;

import java.util.List;

import io.github.astrarre.recipies.v0.api.part.RecipePart;
import io.github.astrarre.recipies.v0.api.recipe.Output;
import io.github.astrarre.recipies.v0.api.recipe.Recipe;

public class RecipeImpl implements Recipe<Output>, Recipe.Mono<Object, Output>, Recipe.Bi<Object, Object, Output> {
	private final List<RecipePart<?, ?>> inputs, outputs;

	public RecipeImpl(List<RecipePart<?, ?>> inputs, List<RecipePart<?, ?>> outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	public Output apply(Object... values) {
		return null;
	}

	@Override
	public Output apply(Object o, Object o2) {
		return null;
	}

	@Override
	public Output apply(Object value) {
		return null;
	}
}
