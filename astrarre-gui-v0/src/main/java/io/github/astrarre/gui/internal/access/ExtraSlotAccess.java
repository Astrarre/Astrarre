package io.github.astrarre.gui.internal.access;

import net.minecraft.item.ItemStack;

public interface ExtraSlotAccess {
	boolean astrarre_isPointOverSlot(double x, double y);

	void setHighlighted(boolean highlighted);

	void setRender(boolean render);

	void setOverride(ItemStack stack);

	int idOverride();
}
