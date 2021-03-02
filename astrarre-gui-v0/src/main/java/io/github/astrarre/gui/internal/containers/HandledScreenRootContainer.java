package io.github.astrarre.gui.internal.containers;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

public class HandledScreenRootContainer extends ScreenRootContainer<HandledScreen<?>> {
	public HandledScreenRootContainer(HandledScreen<?> screen) {
		super(screen);
	}

	public void addSlot(Slot slot) {
		this.screen.getScreenHandler().slots.add(slot);
	}
}
