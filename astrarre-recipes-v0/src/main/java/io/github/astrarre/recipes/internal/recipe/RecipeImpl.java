package io.github.astrarre.recipes.internal.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.Iterables;
import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.recipes.v0.api.recipe.Result;
import io.github.astrarre.util.v0.api.Validate;

public class RecipeImpl
		implements Recipe, Recipe.Bi<Object, Object>, Recipe.Tri<Object, Object, Object>, Recipe.Quad<Object, Object, Object, Object>,
				           Recipe.Pent<Object, Object, Object, Object, Object> {
	public final List<?> list;
	public final String name;
	public final List<RecipePart<?, ?>> parts = new ArrayList<>();
	public List<List<?>> values = new ArrayList<>();

	private final List<Consumer<Recipe>> onReload = new ArrayList<>();

	public RecipeImpl(List<?> list, String name) {
		this.list = list;
		this.name = name;
		for (Object o : list) {
			if(o instanceof RecipePart) {
				this.parts.add((RecipePart<?, ?>) o);
			}
		}
	}

	public void onReload() {
		for (Consumer<Recipe> consumer : this.onReload) {
			consumer.accept(this);
		}
	}

	@Override
	public void addReloadListener(Consumer<Recipe> listener) {
		this.onReload.add(listener);
	}

	@SuppressWarnings ({
			"rawtypes",
			"unchecked"
	})
	@Override
	public Result applyGeneric(Object... inputs) {
		int maxIndex = -1;
		List<?> objects = null;
		outer:
		for (List<?> parameters : this.values) {
			if(parameters.size() != inputs.length) {
				throw new IllegalArgumentException("inputs.length != parameters.length");
			}

			for (int i = 0; i < this.parts.size(); i++) {
				Object input = inputs[i];
				Object val = parameters.get(i);
				RecipePart part = this.parts.get(i);
				if(!part.test(input, val)) {
					if(i > maxIndex) {
						maxIndex = i;
						objects = parameters;
					}
					continue outer;
				}
			}

			objects = parameters;

			for (int i = 0; i < this.parts.size(); i++) {
				RecipePart part = this.parts.get(i);
				part.apply(inputs[i], parameters.get(i));
			}

			return new Result(false, -1, objects);
		}

		return new Result(true, maxIndex, objects);
	}

	@Override
	public <V> Iterable<V> getInputs(RecipePart<V, ?> parser, int index) {
		return Iterables.transform(this.values, i -> {
			Validate.isTrue(Objects.equals(this.parts.get(index), parser), "Wrong part for index!");
			return (V) i.get(index);
		});
	}

	@Override
	public Result apply(Object o, Object o2) {
		return this.applyGeneric(o, o2);
	}

	@Override
	public Result apply(Object o, Object o2, Object o3) {
		return this.applyGeneric(o, o2, o3);
	}

	@Override
	public Result apply(Object o, Object o2, Object o3, Object o4) {
		return this.applyGeneric(o, o2, o3, o4);
	}

	@Override
	public Result apply(Object o, Object o2, Object o3, Object o4, Object o5) {
		return this.applyGeneric(o, o2, o3, o4, o5);
	}
}
