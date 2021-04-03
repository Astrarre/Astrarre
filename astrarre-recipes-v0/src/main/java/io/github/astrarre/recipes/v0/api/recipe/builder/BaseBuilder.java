package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.internal.elements.IdentifiedArrowParser;
import io.github.astrarre.recipes.internal.elements.PlusParser;
import io.github.astrarre.recipes.internal.elements.StandardArrowParser;
import io.github.astrarre.recipes.internal.recipe.RecipeImpl;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.util.Unit;

public abstract class BaseBuilder<Self extends BaseBuilder<Self>> {
	protected final List<?> list;
	protected boolean addPlus = true;

	BaseBuilder(List<?> list) {
		this.list = list;
	}

	/**
	 * starts the outputs segment of this recipe, adds an arrow to the recipe format ('--[namespace:path]->')
	 */
	public Self outputs(Id id) {
		return this.addSeperator(new IdentifiedArrowParser(id));
	}

	/**
	 * adds a separator, an example of a separator is the '+' or '--[myid:thing]->'
	 * @see IdentifiedArrowParser
	 */
	public Self addSeperator(ValueParser<Unit> seperator) {
		List<Object> list = new ArrayList<>(this.list);
		list.add(seperator);
		Self self = this.copy(list);
		self.addPlus = false;
		return self;
	}

	/**
	 * adds a non identified arrow ('-->')
	 */
	public Self outputsNoId() {
		return this.addSeperator(new StandardArrowParser());
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
