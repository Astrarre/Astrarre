package io.github.astrarre.recipe.v0.fabric;

import java.util.Map;

import io.github.astrarre.util.v0.api.event.Event;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public interface RecipePostReloadEvent {
	Event<RecipePostReloadEvent> EVENT = new Event<>(arr -> (manager, recipes) -> {
		for (RecipePostReloadEvent event : arr) {
			event.onReload(manager, recipes);
		}
	}, RecipePostReloadEvent.class);

	/**
	 * @param manager the recipe manager that is reloading
	 * @param recipes an immutable map of all the recipes
	 */
	void onReload(RecipeManager manager, Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes);
}
