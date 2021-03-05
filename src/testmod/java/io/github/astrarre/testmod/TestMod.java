package io.github.astrarre.testmod;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.Button;
import io.github.astrarre.gui.v0.api.base.TextField;
import io.github.astrarre.gui.v0.fabric.adapter.Slot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Transformation;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
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
	public static final Block TEST_BLOCK = Registry.register(Registry.BLOCK,
			"testmod:be_test",
			new TestModBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
	public static final BlockEntityType<?> BE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE,
			"testmod:be_test",
			BlockEntityType.Builder.create(TestModBlock.Entity::new, TEST_BLOCK).build(null));

	@Override
	public void onInitialize() {
		System.out.println(HopperBlockEntity.ABOVE_SHAPE);
		Registry.register(Registry.ITEM, new Identifier("testmod", "test"), new ItemA(new Item.Settings().group(ItemGroup.MISC)));
		//Recipe.getInput(new IntIngredient(), "test");
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
						slot.setTransformation(Transformation.translate(150+i*22, 150, 0));
						container.getContentPanel().add(slot);
					}
				});
			}
			return ActionResult.CONSUME;
		}
	}
}
