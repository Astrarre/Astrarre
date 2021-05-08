package io.github.astrarre.gui.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.screen.ScreenHandlerType;

@Mixin (ScreenHandlerType.class)
public interface ScreenHandlerTypeAccess {
	@Invoker
	static ScreenHandlerType createScreenHandlerType(ScreenHandlerType.Factory factory) { throw new UnsupportedOperationException(); }
}
