package io.github.astrarre.gui.internal.rei;

import me.shedaniel.math.Rectangle;

import net.minecraft.client.gui.Element;

public class RectangleShiftedElement implements Element {
	protected final Element delegate;
	protected final Rectangle bounds;

	public RectangleShiftedElement(Element delegate, Rectangle bounds) {
		this.delegate = delegate;
		this.bounds = bounds;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		mouseX -= this.bounds.x;
		mouseY -= this.bounds.y;
		if (this.isIn(mouseX, mouseY)) {
			this.delegate.mouseMoved(mouseX, mouseY);
		}
	}

	protected boolean isIn(double x, double y) {
		return x >= 0 && y >= 0 && x <= this.bounds.width && y <= this.bounds.height;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		mouseX -= this.bounds.x;
		mouseY -= this.bounds.y;
		return this.isIn(mouseX, mouseY) && this.delegate.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		mouseX -= this.bounds.x;
		mouseY -= this.bounds.y;
		return this.isIn(mouseX, mouseY) && this.delegate.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		mouseX -= this.bounds.x;
		mouseY -= this.bounds.y;
		return this.isIn(mouseX, mouseY) && this.delegate.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		mouseX -= this.bounds.x;
		mouseY -= this.bounds.y;
		return this.isIn(mouseX, mouseY) && this.delegate.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		mouseX -= this.bounds.x;
		mouseY -= this.bounds.y;
		return this.isIn(mouseX, mouseY) && this.delegate.isMouseOver(mouseX, mouseY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.delegate.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.delegate.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.delegate.charTyped(chr, modifiers);
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		return this.delegate.changeFocus(lookForwards);
	}

}
