package io.github.astrarre.recipes.v0.api.recipe;

public interface PartialRecipe extends Recipe {
	void invalidateAll();

	void invalidate(int index);

	boolean isSatisfied(int index);
}
