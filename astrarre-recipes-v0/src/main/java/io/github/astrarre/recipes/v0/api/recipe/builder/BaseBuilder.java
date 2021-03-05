package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.internal.elements.ArrowParser;
import io.github.astrarre.recipes.internal.elements.PlusParser;
import io.github.astrarre.recipes.internal.recipe.RecipeImpl;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.util.v0.api.Id;

public abstract class BaseBuilder<Self extends BaseBuilder<Self>> {
	protected final List<?> list;
	protected boolean addPlus = true;

	BaseBuilder(List<?> list) {
		this.list = list;
	}

	/**
	 * starts the outputs segment of this recipe (adds an arrow)
	 */
	public Self outputs(Id id) {
		List<Object> list = new ArrayList<>(this.list);
		list.add(new ArrowParser(id));
		Self self = this.copy(list);
		self.addPlus = false;
		return self;
	}

	protected abstract Self copy(List<?> objects);

	void addPlus(List<Object> obj) {
		if (this.addPlus) {
			obj.add(PlusParser.INSTANCE);
		}
	}

	<T extends Recipe> T create(String name) {
		return (T) new RecipeImpl(this.list, name);
	}
}
