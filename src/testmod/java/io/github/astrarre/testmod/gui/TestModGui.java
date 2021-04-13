package io.github.astrarre.testmod.gui;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.gui.v0.api.base.borders.ABeveledBorder;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.base.statik.ADarkenedBackground;
import io.github.astrarre.gui.v0.api.base.widgets.AButton;
import io.github.astrarre.gui.v0.api.base.widgets.AProgressBar;
import io.github.astrarre.gui.v0.fabric.adapter.slot.APlayerSlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Transformation;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class TestModGui {

	public static AProgressBar open(ServerPlayerEntity entity) {
		return RootContainer.open((NetworkMember) entity, container -> open(entity, container));
	}

	protected static AProgressBar open(ServerPlayerEntity entity, RootContainer container) {
		// the content panel is the panel of the entire screen. It's origin is at [0, 0] (the top left of the screen)
		APanel contentPanel = container.getContentPanel();
		// we want our gui to be in the middle of the screen, so
		// This object will re-translate itself every time the screen resizes such that the center of the panel is aligned with the center of the screen
		ACenteringPanel center = new ACenteringPanel(175, 165);
		// to emulate minecraft guis, we want to make the background go dark-ish
		contentPanel.add(new ADarkenedBackground());
		contentPanel.add(new ABeveledBorder(center));

		List<ASlot> hotbar = new ArrayList<>();
		for(int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
			for(int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
				ASlot slot = new APlayerSlot(entity.inventory, inventoryColumn + inventoryRow * 9 + 9);
				slot.setTransformation(Transformation.translate(6 + inventoryColumn * 18, 82 + inventoryRow * 18, 0));
				center.add(slot);
				hotbar.add(slot);
			}
		}

		for(int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
			ASlot slot = new Cursed(entity.inventory, hotbarIndex);
			slot.setTransformation(Transformation.translate(6 + hotbarIndex * 18, 140, 0));
			center.add(slot);
			slot.linkAll(container, hotbar);
			for (ASlot hotbarSlot : hotbar) {
				hotbarSlot.link(container, slot);
			}
		}

		AProgressBar bar1 = new AProgressBar(AstrarreIcons.FURNACE_PROGRESS_BAR_FULL, AstrarreIcons.FURNACE_PROGRESS_BAR_EMPTY, AProgressBar.Direction.RIGHT);
		bar1.progress.set(.5f);
		AProgressBar bar2 = new AProgressBar(AstrarreIcons.FURNACE_FLAME_ON, AstrarreIcons.FURNACE_FLAME_OFF, AProgressBar.Direction.UP);
		center.add(bar1);
		center.add(bar2.setTransformation(Transformation.translate(30, 0, 0)));
		center.add(new AButton(AButton.MEDIUM).setTransformation(Transformation.translate(30, 30, 0)));
		//TestDrawable testDrawable = new TestDrawable();
		//center.add(new SimpleBorder(testDrawable).setTransformation(Transformation.translate(10, 10, 0)));
		// here, we create a beveled rectangle. 'Bevel' is an outline, this component is basically just a grey rectangle with a special border (the same one minecraft guis use)
		// we use the shortcut constructor to tell the beveled rectangle to fill up the entire centering panel

		return bar2;
	}

	public static final class Cursed extends APlayerSlot implements Tickable {
		public Cursed(PlayerInventory inventory, int index) {
			super(inventory, index);
			ServerTickEvents.START_SERVER_TICK.register(server -> {
				this.setTransformation(this.getTransformation().combine(Transformation.rotate(0, 0, 1)));
			});
		}

		@Override
		public boolean isTooltipHandled(RootContainer container) {
			return false;
		}

		@Override
		public void tick(RootContainer container) {

		}
	}
}
