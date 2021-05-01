package io.github.astrarre.recipe.internal.vanilla;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class CustomRecipeType<T extends Recipe<?>> implements RecipeType<T> {
	protected final Identifier id;

	public CustomRecipeType(Identifier id) {this.id = id;}

	@Override
	public String toString() {
		return this.id.toString();
	}
}
