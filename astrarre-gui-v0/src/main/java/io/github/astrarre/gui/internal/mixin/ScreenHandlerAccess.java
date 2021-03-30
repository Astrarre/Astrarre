package io.github.astrarre.gui.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

@Mixin (ScreenHandler.class)
public interface ScreenHandlerAccess {
	@Invoker
	Slot callAddSlot(Slot slot);

	@Invoker
	boolean callInsertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast);
}
