package io.github.astrarre.gui.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

@Mixin (HandledScreens.class)
public interface HandledScreensAccess {
	@Invoker
	static void callRegister(ScreenHandlerType type, HandledScreens.Provider provider) { throw new UnsupportedOperationException(); }
}
