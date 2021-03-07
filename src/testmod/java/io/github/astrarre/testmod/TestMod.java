package io.github.astrarre.testmod;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.internal.recipe.RecipeParser;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.testmod.gui.TestModGui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("testmod", "test"), new TestItem(new Item.Settings().group(ItemGroup.MISC)));
		TestModBlock.modInit();
		//Recipe.getInput(new IntIngredient(), "test");
	}

	public static void load(Recipe recipe, String sample) {
		List impls = new ArrayList();
		impls.add(recipe);
		RecipeParser parser = new RecipeParser(impls);
		parser.parseToCompletion(new StringReader(sample), "yeet");
	}

	static class TestItem extends Item {
		public TestItem(Settings settings) {
			super(settings);
		}

		@Override
		@Environment (EnvType.CLIENT)
		public ActionResult useOnBlock(ItemUsageContext context) {
			PlayerEntity entity = context.getPlayer();
			if (!context.getWorld().isClient && entity != null) {
				TestModGui.open((ServerPlayerEntity) entity);
			}
			return ActionResult.CONSUME;
		}
	}
}
