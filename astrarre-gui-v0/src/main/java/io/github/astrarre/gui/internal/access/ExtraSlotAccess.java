package io.github.astrarre.gui.internal.access;

import io.github.astrarre.gui.v0.api.RootContainer;

import net.minecraft.item.ItemStack;

public interface ExtraSlotAccess {
	boolean astrarre_isPointOverSlot(double x, double y);

	void setHighlighted(boolean highlighted);

	void setRender(boolean render);

	void setOverride(ItemStack stack);

	int idOverride();

	ItemStack transferSlot(RootContainer container);
}
