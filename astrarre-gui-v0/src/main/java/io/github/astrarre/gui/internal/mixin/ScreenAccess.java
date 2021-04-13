package io.github.astrarre.gui.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Mixin (Screen.class)
public interface ScreenAccess {
	@Invoker
	void callRenderTooltip(MatrixStack matrices, ItemStack stack, int x, int y);
}
