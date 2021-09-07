package io.github.astrarre.gui.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.screen.slot.Slot;

@Mixin(Slot.class)
public interface SlotAccess {
	@Mutable
	@Accessor("x")
	void setX(int x);

	@Mutable
	@Accessor("x")
	void setY(int y);
}
