package io.github.astrarre.testmod;

import java.util.List;
import java.util.function.Consumer;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.AAggregateDrawable;
import io.github.astrarre.gui.v0.api.base.widgets.AButton;
import io.github.astrarre.gui.v0.api.base.widgets.AScrollBar;
import io.github.astrarre.gui.v0.api.container.ContainerGUI;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.recipe.v0.api.Recipes;
import io.github.astrarre.util.v0.api.Val;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

@Mod("testmod")
public class TestMod {
	public static final List<TestRecipe> RECIPE_LIST = Recipes.createRecipe(new Identifier("testmod:test_recipe"), TestRecipe.class);

	public TestMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(Item.class, (Consumer<RegistryEvent.Register<Item>>) event -> {
			event.getRegistry().register(new TestItem(new Item.Settings().group(ItemGroup.MISC)));
		});
	}

	static class TestItem extends Item {
		public TestItem(Settings settings) {
			super(settings);
			this.setRegistryName(new Identifier("yeet:yeet"));
		}

		@Override
		@OnlyIn(Dist.CLIENT)
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
