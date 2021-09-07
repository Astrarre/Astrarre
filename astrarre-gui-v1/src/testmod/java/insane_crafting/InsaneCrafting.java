package insane_crafting;

import java.util.List;

import io.github.astrarre.recipe.v0.api.Recipes;
import io.github.astrarre.recipe.v0.fabric.RecipePostReloadEvent;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class InsaneCrafting implements ModInitializer {
	public static final List<InsaneCraftingRecipe> RECIPES = Recipes.createRecipe(id("insane_crafting"), InsaneCraftingRecipe.class);

	@Override
	public void onInitialize() {
		RECIPES.add(InsaneCraftingRecipe.TEST);
		Registry.register(Registry.BLOCK, id("insane_crafting"), new InsaneCraftingTable(AbstractBlock.Settings.copy(Blocks.STONE)));
	}

	public static Identifier id(String path) {
		return new Identifier("insane_crafting", path);
	}
}
