package io.github.astrarre.testmod;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.Button;
import io.github.astrarre.gui.v0.api.base.TextField;
import io.github.astrarre.gui.v0.fabric.adapter.Slot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.recipes.internal.recipe.RecipeParser;
import io.github.astrarre.recipes.v0.api.ingredient.Ingredients;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.recipes.v0.api.util.Val;
import io.github.astrarre.recipes.v0.fabric.ingredient.FabricIngredients;
import io.github.astrarre.recipes.v0.fabric.output.FabricOutputs;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("testmod", "test"), new ItemA(new Item.Settings().group(ItemGroup.MISC)));
		TestModBlock.modInit();
		//Recipe.getInput(new IntIngredient(), "test");
	}

	public static void load(Recipe recipe, String sample) {
		List impls = new ArrayList();
		impls.add(recipe);
		RecipeParser parser = new RecipeParser(impls);
		parser.parseToCompletion(new StringReader(sample), "yeet");
	}

	static class ItemA extends Item {
		public ItemA(Settings settings) {
			super(settings);
		}

		@Override
		@Environment (EnvType.CLIENT)
		public ActionResult useOnBlock(ItemUsageContext context) {
			PlayerEntity entity = context.getPlayer();
			if (!context.getWorld().isClient && entity != null) {
				RootContainer.open((NetworkMember) entity, container -> {
					Button button = new Button(container);
					button.setTransformation(Transformation.translate(10, 10, 0).combine(Transformation.rotate(0, 0, 30)));
					container.getContentPanel().add(button);

					TextField field = new TextField(container, 100, 10) {
						@Override
						protected void syncedFromClient(String string) {
							System.out.println("Hello from the server!");
						}
					};
					field.setTransformation(Transformation.translate(30, 30, 0));
					container.getContentPanel().add(field);

					for (int i = 0; i < 9; i++) {
						Slot slot = Slot.inventorySlot(container, entity.inventory, i);
						slot.setTransformation(Transformation.translate(150 + i * 22, 150, 0));
						container.getContentPanel().add(slot);
					}
				});
			}
			return ActionResult.CONSUME;
		}
	}
}
