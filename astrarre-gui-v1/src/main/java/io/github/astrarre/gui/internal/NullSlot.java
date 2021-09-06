package io.github.astrarre.gui.internal;

import net.minecraft.screen.slot.Slot;

public class NullSlot extends Slot {
	public static final NullSlot INSTANCE = new NullSlot();
	private NullSlot() {
		super(NullInventory.INVENTORY, 0, 0, 0);
	}
}
