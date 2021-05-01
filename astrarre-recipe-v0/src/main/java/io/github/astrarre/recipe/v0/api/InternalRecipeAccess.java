package io.github.astrarre.recipe.v0.api;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

/**
 * @deprecated internal
 */
@Deprecated
@ApiStatus.Internal
public class InternalRecipeAccess {
	public static void set(Recipe recipe, Identifier id) {
		recipe.id = id;
	}
}
