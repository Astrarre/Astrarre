package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;

public final class TriBuilder<A, B, C> extends BaseBuilder<TriBuilder<A, B, C>> {
	TriBuilder(List<?> list) {
		super(list);
	}

	@Override
	protected TriBuilder<A, B, C> copy(List<?> objects) {
		return new TriBuilder<>(objects);
	}

	public <D> QuadBuilder<A, B, C, D> add(RecipePart<?, D> part) {
		List<Object> list = new ArrayList<>(this.list);
		this.addPlus(list);
		list.add(part);
		return new QuadBuilder<>(list);
	}

	public Recipe.Tri<A, B, C> build(String name) {
		return this.create(name);
	}
}
