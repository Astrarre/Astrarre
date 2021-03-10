package io.github.astrarre.testmod.gui;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.delegates.SimpleBorder;
import io.github.astrarre.gui.v0.api.panel.CenteringPanel;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.gui.v0.api.statik.BeveledRectangle;
import io.github.astrarre.gui.v0.api.statik.DarkenedBackground;
import io.github.astrarre.gui.v0.api.widgets.ButtonWidget;
import io.github.astrarre.gui.v0.fabric.adapter.Slot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Transformation;

import net.minecraft.server.network.ServerPlayerEntity;
public class TestModGui {
	public static TestDrawable open(ServerPlayerEntity entity) {
		return RootContainer.open((NetworkMember) entity, container -> open(entity, container));
	}

	protected static TestDrawable open(ServerPlayerEntity entity, RootContainer container) {
		// the content panel is the panel of the entire screen. It's origin is at [0, 0] (the top left of the screen)
		Panel contentPanel = container.getContentPanel();
		// to emulate minecraft guis, we want to make the background go dark-ish
		contentPanel.add(new DarkenedBackground());
		// we want our gui to be in the middle of the screen, so
		// This object will re-translate itself every time the screen resizes such that the center of the panel is aligned with the center of the screen
		CenteringPanel center = new CenteringPanel(175, 165);
		contentPanel.add(center);
		// here, we create a beveled rectangle. 'Bevel' is an outline, this component is basically just a grey rectangle with a special border (the same one minecraft guis use)
		// we use the shortcut constructor to tell the beveled rectangle to fill up the entire centering panel
		center.add(new BeveledRectangle(center));

		//center.add(new ButtonWidget(container, ButtonWidget.MEDIUM).setTransformation(Transformation.translate(10, 10, 0)));

		for(int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
			for(int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
				Slot slot = Slot.inventorySlot(
						entity.inventory, inventoryColumn + inventoryRow * 9 + 9);
				slot.setTransformation(Transformation.translate(6 + inventoryColumn * 18, 82 + inventoryRow * 18, 0));
				center.add(slot);
			}
		}

		for(int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
			Slot slot = Slot.inventorySlot(entity.inventory, hotbarIndex);
			slot.setTransformation(Transformation.translate(6 + hotbarIndex * 18, 140, 0));
			center.add(slot);
		}

		TestDrawable testDrawable = new TestDrawable();
		center.add(new SimpleBorder(testDrawable).setTransformation(Transformation.translate(10, 10, 0)));
		return testDrawable;
	}
}
