package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.v0.api.panel.Panel;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TickableElement;

public class PanelElement implements Element, TickableElement {
	protected final Panel panel;
	protected final RootContainerInternal internal;

	public PanelElement(Panel interactable, RootContainerInternal internal) {
		this.panel = interactable;
		this.internal = internal;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.panel.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.panel.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.panel.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return this.panel.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.panel.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.panel.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.panel.charTyped(chr, modifiers);
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		return this.panel.handleFocusCycle(lookForwards);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.panel.mouseHover(mouseX, mouseY);
	}

	@Override
	public void tick() {
		this.internal.tick++;
	}
}