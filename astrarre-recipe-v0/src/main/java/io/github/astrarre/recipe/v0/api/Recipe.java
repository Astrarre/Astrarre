package io.github.astrarre.recipe.v0.api;

public interface Recipe {
	/**
	 * called after the mod has been initialized, can be used to validate and throw errors
	 */
	default void onInit() {}
}
