package io.github.astrarre.testmod;

import io.github.astrarre.gui.v1.api.component.ACenteringPanel;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.button.AButton;
import io.github.astrarre.rendering.v1.api.plane.icon.Icons;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	public final Item item = new Item(new Item.Settings()) {
		@Override
		public ActionResult useOnBlock(ItemUsageContext context) {
			if(context.getWorld().isClient) {
				TestMod.this.openGui();
			}
			return ActionResult.CONSUME;
		}
	};

	public void openGui() {
		ARootPanel root = ARootPanel.open();
		ACenteringPanel panel = new ACenteringPanel(root);
		panel.add(AButton.button(Icons.Groups.button(8, 8), c -> {}).with(Transform3d.translate(10, 10, 0)));
	}

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("astrarre", "testgui0"), this.item);
	}
}
