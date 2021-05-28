package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.v0.api.base.panel.APanel;

import net.minecraft.client.gui.Element;

public class PanelElement implements Element {
	protected final APanel panel;
	protected final RootContainerInternal internal;

	public PanelElement(APanel interactable, RootContainerInternal internal) {
		this.panel = interactable;
		this.internal = internal;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.panel.mouseMoved(this.internal, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.panel.mouseClicked(this.internal, mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.panel.mouseReleased(this.internal, mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.panel.mouseDragged(this.internal, mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return this.panel.mouseScrolled(this.internal, mouseX, mouseY, amount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.panel.keyPressed(this.internal, keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.panel.keyReleased(this.internal, keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.panel.charTyped(this.internal, chr, modifiers);
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		return this.panel.handleFocusCycle(this.internal, lookForwards);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.panel.isHovering(this.internal, mouseX, mouseY);
	}

	public void tick() {
		this.internal.tick++;
		this.internal.tickComponents();
	}
}