package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;

public final class PentBuilder<A, B, C, D, E> extends BaseBuilder<PentBuilder<A, B, C, D, E>> {
	PentBuilder(List<?> list) {
		super(list);
	}

	@Override
	protected PentBuilder<A, B, C, D, E> copy(List<?> objects) {
		return new PentBuilder<>(objects);
	}

	public Builder add(RecipePart<?, ?> part) {
		List<Object> list = new ArrayList<>(this.list);
		this.addPlus(list);
		list.add(part);
		return new Builder(list);
	}

	public Recipe.Pent<A, B, C, D, E> build(String name) {
		return this.create(name);
	}
}
