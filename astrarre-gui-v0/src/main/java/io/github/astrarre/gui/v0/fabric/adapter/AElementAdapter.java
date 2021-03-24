package io.github.astrarre.gui.v0.fabric.adapter;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;

public abstract class AElementAdapter<T extends Drawable & Element> extends ADrawableAdapter<T> {
	public AElementAdapter(DrawableRegistry.Entry id) {
		super(id);
	}

	@Override
	public void mouseMoved(RootContainer container, double mouseX, double mouseY) {
		this.drawable.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		return this.drawable.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(RootContainer container, double mouseX, double mouseY, int button) {
		return this.drawable.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(RootContainer container, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.drawable.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(RootContainer container, double mouseX, double mouseY, double amount) {
		return this.drawable.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean keyPressed(RootContainer container, int keyCode, int scanCode, int modifiers) {
		return this.drawable.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(RootContainer container, int keyCode, int scanCode, int modifiers) {
		return this.drawable.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(RootContainer container, char chr, int modifiers) {
		return this.drawable.charTyped(chr, modifiers);
	}

	@Override
	public boolean handleFocusCycle(RootContainer container, boolean forward) {
		return this.drawable.changeFocus(forward);
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return this.drawable.isMouseOver(mouseX, mouseY) && super.isHovering(container, mouseX, mouseY);
	}
}
