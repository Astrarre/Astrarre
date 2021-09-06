import io.github.astrarre.gui.v1.api.component.AList;
import io.github.astrarre.gui.v1.api.component.ASlot;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.rendering.v1.api.util.Axis2d;
import test.HugeChestBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class GuiTestMod implements ModInitializer {
	/**
	 * this is the maximum 'guaranteed' window in which you can render for GUIs
	 * In auto gui mode (in video settings) will rescale the coordinate grid to ensure that this 'window' in the center of the screen is always visible.
	 * For normal GUIs (centered guis, like inventories for example): it's recommended to use this scale.
	 */
	public static final int MAX_SAFE_WIDTH = 320, MAX_SAFE_HEIGHT = 240;
	final Item h = new MyItem();
	// 21x40
	// 840 is max in theory
	// 819 (21x39) is prolly good enough for margins

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("mymod:y"), h);
		Registry.register(Registry.BLOCK, new Identifier("mymod:z"), new HugeChestBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
	}

	private static class MyItem extends Item {
		public MyItem() {super(new Settings());}

		@Override
		public ActionResult useOnBlock(ItemUsageContext context) {
			PlayerEntity entity = context.getPlayer();
			if(entity != null) {
				try {
					extracted(context, entity);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			return super.useOnBlock(context);
		}
	}

	private static void extracted(ItemUsageContext context, PlayerEntity entity) {
		var keys = ASlot.inv(entity.getInventory(), 0);
		ServerPanel.openHandled(entity, (communication, panel) -> {
			AList list = new AList(Axis2d.X, 1);
			for(ASlot.Key key : keys) {
				list.add(new ASlot(communication, panel, key));
			}
			panel.add(list);
		}, (communication, panel) -> ASlot.linkAll(communication, panel, keys));
	}
}
