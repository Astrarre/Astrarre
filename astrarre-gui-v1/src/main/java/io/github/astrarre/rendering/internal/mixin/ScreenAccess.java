package io.github.astrarre.rendering.internal.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(Screen.class)
public interface ScreenAccess {
	@Invoker
	void callRenderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y);

	@Invoker
	<T extends Element & Drawable & Selectable> T callAddDrawableChild(T drawableElement);
}
