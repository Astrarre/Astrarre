package io.github.astrarre.testmod;

import io.github.astrarre.testmod.gui.TestDrawable;
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

	static class TestItem extends Item {
		public TestItem(Settings settings) {
			super(settings);
		}

		@Override
		@Environment (EnvType.CLIENT)
		public ActionResult useOnBlock(ItemUsageContext context) {
			PlayerEntity entity = context.getPlayer();
			if (!context.getWorld().isClient && entity != null) {
				TestDrawable drawable = TestModGui.open((ServerPlayerEntity) entity);
				drawable.power.set((int) context.getHitPos().y);
			}
			return ActionResult.CONSUME;
		}
	}
}
