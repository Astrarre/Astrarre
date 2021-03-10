package io.github.astrarre.gui.v0.fabric.adapter;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;

public abstract class ElementAdapter<T extends Drawable & Element> extends DrawableAdapter<T> {
	public ElementAdapter(RootContainer rootContainer, DrawableRegistry.Entry id) {
		super(rootContainer, id);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.drawable.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.drawable.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.drawable.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.drawable.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return this.drawable.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.drawable.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.drawable.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.drawable.charTyped(chr, modifiers);
	}

	@Override
	public boolean handleFocusCycle(boolean forward) {
		return this.drawable.changeFocus(forward);
	}

	@Override
	public boolean isHovering(double mouseX, double mouseY) {
		return this.drawable.isMouseOver(mouseX, mouseY) && super.isHovering(mouseX, mouseY);
	}
}
