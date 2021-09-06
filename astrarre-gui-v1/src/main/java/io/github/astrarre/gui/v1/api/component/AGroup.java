package io.github.astrarre.gui.v1.api.component;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;
import io.github.astrarre.gui.internal.CursorImpl;
import io.github.astrarre.gui.v1.api.listener.cursor.ClickType;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.listener.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.listener.focus.FocusHandler;
import io.github.astrarre.gui.v1.api.listener.keyboard.Key;
import io.github.astrarre.gui.v1.api.listener.keyboard.KeyboardListener;
import io.github.astrarre.gui.v1.api.listener.keyboard.Modifier;
import io.github.astrarre.gui.v1.api.util.ComponentTransform;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import org.jetbrains.annotations.Nullable;

/**
 * A list of {@link AComponent}s
 *
 * @see APanel
 */
public abstract class AGroup extends AComponent implements KeyboardListener, MouseListener, Iterable<ComponentTransform<?>> {
	AComponent focused;

	protected AGroup() {
		this.lockBounds(true);
	}

	@Override
	public boolean inBounds(float x, float y) {
		for(ComponentTransform<?> cmp : this) {
			if(cmp.component().inBounds(cmp.localizeX(x, y), cmp.localizeY(x, y))) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		for(ComponentTransform<?> component : this) {
			if(component.transform() == Transform3d.IDENTITY) {
				component.component().render(cursor, render);
			} else {
				var transformed = cursor.transformed(component.transform().invert());
				try(var ignore = render.transform(component.transform())) {
					component.component().render(transformed, render);
				}
			}
		}
	}

	@Override
	public boolean next(FocusDirection direction) {
		if(direction.isForward()) {
			if(this.focused == null) {
				var first = Iterables.getFirst(this, null);
				this.focused = first != null ? first.component() : null;
			}

			do {
				if(this.focused instanceof FocusHandler f && f.next(direction)) {
					return true;
				}
			} while((this.focused = this.after(this.focused)) != null);
		} else {
			if(this.focused == null) {
				var first = Iterables.getLast(this, null);
				this.focused = first != null ? first.component() : null;
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
	public boolean mouseScrolled(Cursor cursor, float scroll) {
		return this.cursor(cursor, (transformed, component, listener) -> listener.mouseScrolled(transformed, scroll));
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

	@Override
	public boolean capturesInput() {
		return this.keyboard((component, listener) -> listener.capturesInput());
	}

	public AComponent getAtRecursive(float x, float y) {
		CursorImpl impl = new CursorImpl(x, y);
		for(ComponentTransform<?> component : this) {
			AComponent c = component.component();
			Cursor transformed = impl.transformed(component.transform().invert());
			if(c instanceof AGroup a) {
				var val = a.getAtRecursive(component.localizeX(x, y), component.localizeY(x, y));
				if(val != null) {
					return val;
				}
			} else if(c.isIn(transformed)) {
				return c;
			}
		}
		return null;
	}

	protected boolean cursor(Cursor cursor, CursorCallback consumer) {
		for(ComponentTransform<?> component : this) {
			AComponent c = component.component();
			if(c instanceof MouseListener l && !c.is(AComponent.SKIP_MOUSEVENT)) {
				Cursor transformed = cursor.transformed(component.transform().invert());
				if(c.isIn(transformed) && consumer.accept(transformed, component, l)) {
					return true;
				}
			}
		}
		return false;
	}

	@Nullable
	protected abstract AComponent before(AComponent component);

	@Nullable
	protected AComponent after(AComponent component) {
		Iterator<ComponentTransform<?>> iter = this.iterator();
		while(iter.hasNext()) {
			var cmp = iter.next();
			if(component == cmp.component() && iter.hasNext()) {
				return iter.next().component();
			}
		}
		return null;
	}

	boolean focused(AComponent focused, Predicate<ComponentTransform<?>> transform) {
		if(focused == null) {
			return false;
		}
		for(var form : this) {
			if(form.component() == focused) {
				return transform.test(form);
			} else if(form.component() instanceof AGroup a) {
				a.focused(focused, t -> transform.test(t.with(form.transform().andThen(t.transform()))));
			}
		}
		return false;
	}

	protected boolean keyboard(KeyboardCallback callback) {
		if(this.focused != null && this.focused instanceof KeyboardListener l && callback.accept(this.focused, l)) {
			return true;
		}

		for(ComponentTransform<?> component : this) {
			if(this.focused != component.component() && component.component() instanceof KeyboardListener l) {
				if(callback.accept(component.component(), l)) {
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
		boolean accept(Cursor transformed, ComponentTransform<?> component, MouseListener listener);
	}
}
