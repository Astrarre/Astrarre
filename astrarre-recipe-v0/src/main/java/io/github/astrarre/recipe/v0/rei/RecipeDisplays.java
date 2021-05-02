package io.github.astrarre.recipe.v0.rei;

import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.recipe.v0.api.Recipe;
import io.github.astrarre.util.v0.api.Id;
import me.shedaniel.rei.api.RecipeDisplay;

/**
 * @see RecipeDisplayBuilder
 */
public class RecipeDisplays {
	public static final FunctionAccess<Recipe, RecipeDisplay> REI_COMPATIBILITY = new FunctionAccess<>(Id.create("astrarre-recipe-v0", "rei_compat"));
	static {
		REI_COMPATIBILITY.addProviderFunction();
	}
}
