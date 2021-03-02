package io.github.astrarre.gui.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;

@Mixin (HandledScreen.class)
public interface HandledScreenAccessor {
	@Invoker
	void callDrawSlot(MatrixStack matrices, Slot slot);
}
