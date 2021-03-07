package io.github.astrarre.testmod;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.BeveledRectangle;
import io.github.astrarre.gui.v0.api.base.CenteringPanel;
import io.github.astrarre.gui.v0.api.base.DarkenedBackground;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.gui.v0.fabric.adapter.Slot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.recipes.internal.recipe.RecipeParser;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.rendering.v0.api.Transformation;

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
					Panel contentPanel = container.getContentPanel();
					contentPanel.add(new DarkenedBackground(container));
					CenteringPanel panel = new CenteringPanel(container, 175, 165);
					contentPanel.add(panel);
					panel.add(new BeveledRectangle(container, panel));

					for(int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
						for(int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
							Slot slot = Slot.inventorySlot(container,
									entity.inventory, inventoryColumn + inventoryRow * 9 + 9);
							slot.setTransformation(Transformation.translate(6 + inventoryColumn * 18, 82 + inventoryRow * 18, 0));
							panel.add(slot);
						}
					}

					for(int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
						Slot slot = Slot.inventorySlot(container, entity.inventory, hotbarIndex);
						slot.setTransformation(Transformation.translate(6 + hotbarIndex * 18, 140, 0));
						panel.add(slot);
					}
				});
			}
			return ActionResult.CONSUME;
		}
	}
}
