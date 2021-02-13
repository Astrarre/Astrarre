package io.github.astrarre.testmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("testmod", "test"), new ItemA(new Item.Settings()));
	}

	static class ItemA extends Item {
		public ItemA(Settings settings) {
			super(settings);
		}

		@Override
		@Environment (EnvType.CLIENT)
		public ActionResult useOnBlock(ItemUsageContext context) {
			MinecraftClient.getInstance().openScreen(new AstrarreScreen(new LiteralText("aaaaaaa")));
			return ActionResult.CONSUME;
		}
	}
}
