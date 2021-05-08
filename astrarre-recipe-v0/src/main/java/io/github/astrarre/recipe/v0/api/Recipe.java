package io.github.astrarre.recipe.v0.api;


import net.minecraft.util.Identifier;

/**
 * @see Recipes
 */
public abstract class Recipe {
	volatile Identifier id = null;

	/**
	 * called after the recipe has been initialized, can be used to validate and throw errors
	 */
	public void onInit() {}

	public Identifier getId() {
		return this.id;
	}
}
