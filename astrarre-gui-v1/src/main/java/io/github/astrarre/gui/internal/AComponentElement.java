package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.v1.api.cursor.ClickType;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.keyboard.KeyboardListener;

import net.minecraft.client.gui.Element;

/**
 * does not respect transformation
 */
public interface AComponentElement extends Element {
	<T extends MouseListener & KeyboardListener> T listener();

	Cursor createCursor(double mouseX, double mouseY);

	Cursor currentCursor();

	@Override
	default boolean changeFocus(boolean lookForwards) {
		return this.listener().next(lookForwards ? FocusDirection.FORWARD : FocusDirection.BACKWARDS);
	}

	@Override
	default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.listener().onKeyPressed(GuiInternal.keyByGlfwId(keyCode), scanCode, GuiInternal.modifiersByGlfwFlags(modifiers));
	}

	@Override
	default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.listener().onKeyReleased(GuiInternal.keyByGlfwId(keyCode), scanCode, GuiInternal.modifiersByGlfwFlags(modifiers));
	}

	@Override
	default boolean charTyped(char chr, int modifiers) {
		return this.listener().onTypedChar(chr, GuiInternal.modifiersByGlfwFlags(modifiers));
	}

	@Override
	default void mouseMoved(double mouseX, double mouseY) {
		Cursor curr = this.currentCursor(), at = this.createCursor(mouseX, mouseY);
		float deltaX = (float) mouseX - curr.x(), deltaY = (float) mouseY - curr.y();
		if(curr.isPressed(ClickType.Standard.LEFT)) {
			this.listener().mouseDragged(at, ClickType.Standard.LEFT, deltaX, deltaY);
		}
		this.listener().mouseMoved(at, (float) mouseX - curr.x(), (float) mouseY - curr.y());
	}

	@Override
	default boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.listener().mouseClicked(this.createCursor(mouseX, mouseY), GuiInternal.clickTypeByGlfwId(button));
	}

	@Override
	default boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.listener().mouseReleased(this.createCursor(mouseX, mouseY), GuiInternal.clickTypeByGlfwId(button));
	}

	@Override
	default boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return this.listener().mouseScrolled(this.createCursor(mouseX, mouseY), amount);
	}

	@Override
	default boolean isMouseOver(double mouseX, double mouseY) {
		return this.listener().isIn(this.createCursor(mouseX, mouseY));
	}
}
