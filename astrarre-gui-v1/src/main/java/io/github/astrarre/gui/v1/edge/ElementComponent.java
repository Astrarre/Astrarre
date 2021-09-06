package io.github.astrarre.gui.v1.edge;

import java.util.Set;

import io.github.astrarre.gui.internal.GuiInternal;
import io.github.astrarre.gui.v1.api.listener.cursor.ClickType;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.listener.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.listener.keyboard.Key;
import io.github.astrarre.gui.v1.api.listener.keyboard.KeyboardListener;
import io.github.astrarre.gui.v1.api.listener.keyboard.Modifier;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;

/**
 * Allows minecraft's components to be used in astrarre's gui API
 */
public class ElementComponent extends DrawableComponent implements KeyboardListener, MouseListener {
	<T extends Drawable & Element> ElementComponent(T drawable) {
		super(drawable);
		this.lockBounds(true); // bounds don't really work for elements
	}

	@Override
	public void mouseMoved(Cursor cursor, float deltaX, float deltaY) {
		this.e().mouseMoved(cursor.x(), cursor.y());
	}

	@Override
	public boolean mouseClicked(Cursor cursor, ClickType type) {
		return this.e().mouseClicked(cursor.x(), cursor.y(), type.glfwId());
	}

	@Override
	public boolean mouseReleased(Cursor cursor, ClickType type) {
		return this.e().mouseReleased(cursor.x(), cursor.y(), type.glfwId());
	}

	@Override
	public boolean mouseDragged(Cursor cursor, ClickType type, float deltaX, float deltaY) {
		return this.e().mouseDragged(cursor.x(), cursor.y(), type.glfwId(), deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(Cursor cursor, float scroll) {
		return this.e().mouseScrolled(cursor.x(), cursor.y(), scroll);
	}

	final Element e() {
		return (Element) this.drawable;
	}

	@Override
	public boolean inBounds(float x, float y) {
		return this.e().isMouseOver(x, y);
	}

	@Override
	public boolean next(FocusDirection direction) {
		return this.e().changeFocus(direction.isForward());
	}

	@Override
	public boolean onKeyPressed(Key key, int scanCode, Set<Modifier> modifiers) {
		return this.e().keyPressed(key.glfwId(), scanCode, GuiInternal.extractFlags(modifiers));
	}

	@Override
	public boolean onKeyReleased(Key key, int scanCode, Set<Modifier> modifiers) {
		return this.e().keyReleased(key.glfwId(), scanCode, GuiInternal.extractFlags(modifiers));
	}

	@Override
	public boolean onTypedChar(char chr, Set<Modifier> modifiers) {
		return this.e().charTyped(chr, GuiInternal.extractFlags(modifiers));
	}
}
