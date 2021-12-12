package testmod;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.github.astrarre.recipe.v0.api.Recipe;
import io.github.astrarre.recipe.v0.api.Recipes;
import io.github.astrarre.util.v0.fabric.Tags;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class TestMod implements ModInitializer {
	public static final List<TestRecipe> TEST_RECIPE = Recipes.createRecipe(new Identifier("testmod:test_recipe"), TestRecipe.class);

	public static class TestRecipe extends Recipe {
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
			System.out.println(this.getId());
		}
	}

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("testmod:test_item"), new TestItem(new Item.Settings()));
	}

	public static class TestItem extends Item {
		public TestItem(Settings settings) {
			super(settings);
		}

		@Override
		public ActionResult useOnBlock(ItemUsageContext context) {
			if(!context.getWorld().isClient) {
				System.out.println(TEST_RECIPE);
				for (TestRecipe recipe : TEST_RECIPE) {
					System.out.println("\t" + recipe + " " + Tags.get(recipe.tag));
				}
				return ActionResult.CONSUME;
			}
			return super.useOnBlock(context);
		}
	}


}
