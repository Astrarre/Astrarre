package io.github.astrarre.gui.v1.api.component;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterables;
import io.github.astrarre.gui.v1.api.AComponent;
import io.github.astrarre.gui.v1.api.FocusableComponent;
import io.github.astrarre.gui.v1.api.cursor.ClickType;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.focus.FocusHandler;
import io.github.astrarre.gui.v1.api.keyboard.Key;
import io.github.astrarre.gui.v1.api.keyboard.KeyboardListener;
import io.github.astrarre.gui.v1.api.keyboard.Modifier;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import org.jetbrains.annotations.Nullable;

/**
 * A list of {@link AComponent}s
 *
 * @see APanel
 */
public abstract class AGroup extends AComponent implements KeyboardListener, MouseListener, Iterable<AComponent> {
	AComponent focused;

	protected AGroup() {
		this.lockBounds();
	}

	@Override
	public boolean inBounds(float x, float y) {
		for(AComponent component : this) {
			if(component.inBounds(component.localizeX(x, y), component.localizeY(x, y))) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		for(AComponent component : this) {
			component.render(cursor, render);
		}
	}


	@Override
	public boolean next(FocusDirection direction) {
		if(direction.isForward()) {
			if(this.focused == null) {
				this.focused = Iterables.getFirst(this, null);
			}

			do {
				if(this.focused instanceof FocusHandler f && f.next(direction)) {
					return true;
				}
			} while((this.focused = this.after(this.focused)) != null);
		} else {
			if(this.focused == null) {
				this.focused = Iterables.getLast(this, null);
			}

			do {
				if(this.focused instanceof FocusHandler f && f.next(direction)) {
					return true;
				}
			} while((this.focused = this.before(this.focused)) != null);
		}
		return false;
	}

	@Override
	public void mouseMoved(Cursor cursor, float deltaX, float deltaY) {
		float oldX = cursor.x() - deltaX, oldY = cursor.y() - deltaY;
		this.cursor(cursor, (transformed, component, listener) -> {
			float localX = component.localizeX(oldX, oldY), localY = component.localizeY(oldX, oldY);
			listener.mouseMoved(transformed, transformed.x() - localX, transformed.y() - localY);
			return false;
		});
	}

	@Override
	public boolean mouseClicked(Cursor cursor, ClickType type) {
		return this.cursor(cursor, (transformed, component, listener) -> listener.mouseClicked(transformed, type));
	}

	@Override
	public boolean mouseReleased(Cursor cursor, ClickType type) {
		return this.cursor(cursor, (transformed, component, listener) -> listener.mouseReleased(transformed, type));
	}

	@Override
	public boolean mouseDragged(Cursor cursor, ClickType type, float deltaX, float deltaY) {
		float oldX = cursor.x() - deltaX, oldY = cursor.y() - deltaY;
		return this.cursor(cursor, (transformed, component, listener) -> {
			float localX = component.localizeX(oldX, oldY), localY = component.localizeY(oldX, oldY);
			return listener.mouseDragged(transformed, type, transformed.x() - localX, transformed.y() - localY);
		});
	}

	@Override
	public boolean mouseScrolled(Cursor cursor, double scroll) {
		return this.cursor(cursor, (transformed, component, listener) -> listener.mouseScrolled(transformed, scroll));
	}

	@Override
	public boolean capturesInput() {
		return this.keyboard((component, listener) -> listener.capturesInput());
	}

	@Override
	public boolean onKeyPressed(Key key, int scanCode, Set<Modifier> modifiers) {
		return this.keyboard((component, listener) -> listener.onKeyPressed(key, scanCode, modifiers));
	}

	@Override
	public boolean onKeyReleased(Key key, int scanCode, Set<Modifier> modifiers) {
		return this.keyboard((component, listener) -> listener.onKeyReleased(key, scanCode, modifiers));
	}

	@Override
	public boolean onTypedChar(char chr, Set<Modifier> modifiers) {
		return this.keyboard((component, listener) -> listener.onTypedChar(chr, modifiers));
	}

	@Nullable
	protected abstract AComponent before(AComponent component);

	@Nullable
	protected AComponent after(AComponent component) {
		Iterator<AComponent> iter = this.iterator();
		while(iter.hasNext()) {
			AComponent cmp = iter.next();
			if(component == cmp) {
				return iter.next();
			}
		}
		return null;
	}

	protected boolean cursor(Cursor cursor, CursorCallback consumer) {
		for(AComponent component : this) {
			if(component instanceof MouseListener l) {
				Cursor transformed = cursor.transformed(component.getTransform());
				if(component.isIn(transformed) && consumer.accept(transformed, component, l)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean keyboard(KeyboardCallback callback) {
		if(this.focused instanceof KeyboardListener l && callback.accept(this.focused, l)) {
			return true;
		}

		for(AComponent component : this) {
			if(this.focused != component && component instanceof KeyboardListener l) {
				if(callback.accept(component, l)) {
					return true;
				}
			}
		}

		return false;
	}

	protected interface KeyboardCallback {
		boolean accept(AComponent component, KeyboardListener listener);
	}

	protected interface CursorCallback {
		boolean accept(Cursor transformed, AComponent component, MouseListener listener);
	}
}
