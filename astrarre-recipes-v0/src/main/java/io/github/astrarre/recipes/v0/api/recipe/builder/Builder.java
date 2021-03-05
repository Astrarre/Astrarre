package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;

class Builder extends BaseBuilder<Builder> {

	Builder(List<?> list) {
		super(list);
	}

	public Builder addInput(RecipePart<?, ?> part) {
		List<Object> list = new ArrayList<>(this.list);
		this.addPlus(list);
		list.add(part);
		return new Builder(list);
	}

	public Recipe build(String name) {
		return this.create(name);
	}

	@Override
	protected Builder copy(List<?> objects) {
		return new Builder(objects);
	}
}