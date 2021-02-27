package io.github.astrarre.recipies.v0.api.ingredient;

import io.github.astrarre.recipies.v0.api.io.CharInput;

/**
 * An input (or output) parser of a recipe
 * @param <V>
 * @param <I>
 */
public interface RecipeComponentParser<V, I> {
	/**
	 * @return the number of chars until the end of the ingredient, return -1 if invalid
	 */
	int end(CharInput input);

	/**
	 * @return the information of this ingredient
	 */
	V parse(CharInput input);

	/**
	 * @param value the 'ingredient' (eg. ItemStack[])
	 * @param input the 'inventory' (eg. CraftingInventory)
	 * @return true if the input was satisfactory
	 */
	boolean apply(V value, I input);
}
