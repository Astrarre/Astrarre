package testmod;

import java.util.List;

import io.github.astrarre.recipe.v0.api.Recipe;
import io.github.astrarre.recipe.v0.api.Recipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	public static final List<TestRecipe> TEST_RECIPE = Recipes.createRecipe(new Identifier("testmod:test_recipe"), TestRecipe.class);

	@Override
	public void onInitialize() {
	}

	public static class TestRecipe implements Recipe {
		public final Tag<Block> tag;
		public final Ingredient ingredient;
		public final ItemStack stack;

		public TestRecipe(Tag<Block> tag, Ingredient ingredient, ItemStack stack) {
			this.tag = tag;
			this.ingredient = ingredient;
			this.stack = stack;
		}

		@Override
		public void onInit() {
		}
	}
}
