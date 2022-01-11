package testmod;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.astrarre.recipe.v0.api.Recipes;
import io.github.astrarre.recipe.v0.api.Reloadable;
import io.github.astrarre.recipe.v0.api.ReloadableList;
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

public class TestMod implements ModInitializer {
	public static final ReloadableList<TestRecipe> TEST_RECIPE = Recipes.createRecipe(new Identifier("testmod:test_recipe"), TestRecipe.class);
	public record TestRecipe(Tag<Block> tag, Ingredient ingredient, ItemStack stack) {}

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
