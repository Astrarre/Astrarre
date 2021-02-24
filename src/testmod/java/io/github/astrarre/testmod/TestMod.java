package io.github.astrarre.testmod;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	public static final Block TEST_BLOCK = Registry.register(Registry.BLOCK, "testmod:be_test", new TestModBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
	public static final BlockEntityType<?> BE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, "testmod:be_test", BlockEntityType.Builder.create(TestModBlock.Entity::new, TEST_BLOCK).build(null));

	@Override
	public void onInitialize() {
		System.out.println(HopperBlockEntity.ABOVE_SHAPE);
		Registry.register(Registry.ITEM, new Identifier("testmod", "test"), new ItemA(new Item.Settings().group(ItemGroup.MISC)));
		System.exit(0);
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
