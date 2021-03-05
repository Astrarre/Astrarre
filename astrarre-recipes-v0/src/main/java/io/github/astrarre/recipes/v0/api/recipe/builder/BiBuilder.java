package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;

public final class BiBuilder<A, B> extends BaseBuilder<BiBuilder<A, B>> {
	BiBuilder(List<?> list) {
		super(list);
	}

	public <C> TriBuilder<A, B, C> add(RecipePart<?, C> part) {
		List<Object> list = new ArrayList<>(this.list);
		this.addPlus(list);
		list.add(part);
		return new TriBuilder<>(list);
	}

	@Override
	protected BiBuilder<A, B> copy(List<?> objects) {
		return new BiBuilder<>(objects);
	}

	public Recipe.Bi<A, B> build(String name) {
		return this.create(name);
	}
}
