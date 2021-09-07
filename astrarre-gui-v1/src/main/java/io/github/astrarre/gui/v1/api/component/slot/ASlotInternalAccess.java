package io.github.astrarre.gui.v1.api.component.slot;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

/**
 * @deprecated internal
 */
@Deprecated
@ApiStatus.Internal
public class ASlotInternalAccess {
	public static Slot getSlot(ASlot slot) {
		return slot.slot;
	}

	public static void setRender(ASlot slot, ItemStack stack, boolean highlighted) {
		slot.toRender = stack;
		slot.highlightOverride = highlighted;
	}

	public static SlotKey getKey(ASlot slot) {
		return slot.key;
	}
}
