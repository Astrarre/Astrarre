package testmod;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.network.NetworkMember;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, "testmod:testitem", new TestItem(new Item.Settings()));
	}

	public static class TestItem extends Item {
		public TestItem(Settings settings) {
			super(settings);
		}

		@Override
		public ActionResult useOnBlock(ItemUsageContext context) {
			if(!context.getWorld().isClient) {
				NetworkMember member = (NetworkMember) context.getPlayer();
				RootContainer.openContainer(member, TestContainerGui::new);

			}
			return super.useOnBlock(context);
		}
	}
}
