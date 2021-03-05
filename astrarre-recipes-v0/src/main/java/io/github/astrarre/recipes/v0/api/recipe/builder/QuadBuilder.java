package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;

public final class QuadBuilder<A, B, C, D> extends BaseBuilder<QuadBuilder<A, B, C, D>> {
	QuadBuilder(List<?> list) {
		super(list);
	}

	public <E> PentBuilder<A, B, C, D, E> add(RecipePart<?, E> part) {
		List<Object> list = new ArrayList<>(this.list);
		this.addPlus(list);
		list.add(part);
		return new PentBuilder<>(list);
	}

	@Override
	protected QuadBuilder<A, B, C, D> copy(List<?> objects) {
		return new QuadBuilder<>(objects);
	}

	public Recipe.Quad<A, B, C, D> build(String name) {
		return this.create(name);
	}
}
