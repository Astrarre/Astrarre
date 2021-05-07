package io.github.astrarre.testmod;

import java.util.List;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.AAggregateDrawable;
import io.github.astrarre.gui.v0.api.base.AWindowDrawable;
import io.github.astrarre.gui.v0.api.base.widgets.AButton;
import io.github.astrarre.gui.v0.api.base.widgets.AScrollBar;
import io.github.astrarre.gui.v0.api.container.ContainerGUI;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.recipe.v0.api.Recipes;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.util.v0.api.Val;

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
						RootContainer.openContainer((NetworkMember) entity, Contater::new);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ActionResult.CONSUME;
		}
	}

	public static final class Contater extends ContainerGUI {
		public Contater(RootContainer container, NetworkMember member) {
			super(container, member, 175, 165);
		}

		@Override
		protected void addGui(AAggregateDrawable panel, int width, int height, List<ASlot> playerSlots) {
			AScrollBar scrollBar = new AScrollBar(new AButton(AButton.MEDIUM), Val.ofFloat(0), 40);
			panel.add(scrollBar);
		}
	}
}
