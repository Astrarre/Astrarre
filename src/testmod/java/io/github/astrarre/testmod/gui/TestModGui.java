package io.github.astrarre.testmod.gui;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.borders.ABeveledBorder;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.base.statik.ADarkenedBackground;
import io.github.astrarre.gui.v0.api.base.widgets.list.ScrollBar;
import io.github.astrarre.gui.v0.api.base.widgets.list.VerticalListWidget;
import io.github.astrarre.gui.v0.fabric.adapter.slot.PlayerSlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.Slot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Transformation;

import net.minecraft.server.network.ServerPlayerEntity;
public class TestModGui {

	public static TestDrawable open(ServerPlayerEntity entity) {
		return RootContainer.open((NetworkMember) entity, container -> open(entity, container));
	}

	protected static TestDrawable open(ServerPlayerEntity entity, RootContainer container) {
		// the content panel is the panel of the entire screen. It's origin is at [0, 0] (the top left of the screen)
		APanel contentPanel = container.getContentPanel();
		// we want our gui to be in the middle of the screen, so
		// This object will re-translate itself every time the screen resizes such that the center of the panel is aligned with the center of the screen
		ACenteringPanel center = new ACenteringPanel(175, 165);
		// to emulate minecraft guis, we want to make the background go dark-ish
		contentPanel.add(new ADarkenedBackground());
		contentPanel.add(new ABeveledBorder(center));

		for(int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
			for(int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
				Slot slot = new PlayerSlot(entity.inventory, inventoryColumn + inventoryRow * 9 + 9);
				slot.setTransformation(Transformation.translate(6 + inventoryColumn * 18, 82 + inventoryRow * 18, 0));
				center.add(slot);
			}
		}

		for(int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
			Slot slot = new PlayerSlot(entity.inventory, hotbarIndex);
			slot.setTransformation(Transformation.translate(6 + hotbarIndex * 18, 140, 0));
			center.add(slot);
		}

		ScrollBar scrollBar = new ScrollBar(60, 10);
		center.add(scrollBar);

		//TestDrawable testDrawable = new TestDrawable();
		//center.add(new SimpleBorder(testDrawable).setTransformation(Transformation.translate(10, 10, 0)));
		// here, we create a beveled rectangle. 'Bevel' is an outline, this component is basically just a grey rectangle with a special border (the same one minecraft guis use)
		// we use the shortcut constructor to tell the beveled rectangle to fill up the entire centering panel

		return null;
	}
}
