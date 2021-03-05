package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;

public final class MonoBuilder<A> extends BaseBuilder<MonoBuilder<A>> {
	MonoBuilder(List<?> list) {
		super(list);
	}

	@Override
	protected MonoBuilder<A> copy(List<?> objects) {
		return new MonoBuilder<>(objects);
	}

	public <B> BiBuilder<A, B> add(RecipePart<?, B> part) {
		List<Object> list = new ArrayList<>(this.list);
		this.addPlus(list);
		list.add(part);
		return new BiBuilder<>(list);
	}

	public Recipe.Mono<A> build(String name) {
		return this.create(name);
	}
}