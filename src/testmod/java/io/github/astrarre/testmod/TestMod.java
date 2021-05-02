package io.github.astrarre.testmod;

import java.util.List;

import io.github.astrarre.recipe.v0.api.Recipes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	public static final List<TestRecipe> RECIPE_LIST = Recipes.createRecipe(new Identifier("testmod:test_recipe"), TestRecipe.class);
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("testmod", "test"), new TestItem(new Item.Settings().group(ItemGroup.MISC)));
	//	TestModBlock.modInit();
		//Recipe.getInput(new IntIngredient(), "test");
	}

	static class TestItem extends Item {
		public TestItem(Settings settings) {
			super(settings);
		}

		@Override
		@Environment (EnvType.CLIENT)
		public ActionResult useOnBlock(ItemUsageContext context) {
			PlayerEntity entity = context.getPlayer();
			if(entity != null) {
				try {
					if (!context.getWorld().isClient) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ActionResult.CONSUME;
		}
	}
}
