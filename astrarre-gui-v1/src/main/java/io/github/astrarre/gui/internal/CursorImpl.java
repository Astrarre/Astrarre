package io.github.astrarre.gui.internal;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import io.github.astrarre.gui.v1.api.cursor.ClickType;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.cursor.CursorType;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.util.Point2f;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;

public class CursorImpl implements Cursor {
	public static CursorType type = CursorType.Standard.ARROW;

	public final Map<Key<?>, Object> map;
	public float x, y;

	public final Predicate<ClickType> pressed;

	public CursorImpl(Map<Key<?>, Object> map, float x, float y, Predicate<ClickType> pressed) {
		this.map = map;
		this.x = x;
		this.y = y;
		this.pressed = pressed;
	}

	public CursorImpl(CursorImpl impl) {
		this(impl.map, impl.x, impl.y, impl.pressed);
	}

	@Override
	public float x() {
		return this.x;
	}

	@Override
	public float y() {
		return this.y;
	}

	@Override
	public CursorType getType() {
		return type;
	}

	@Override
	public void setType(CursorType type) {
		CursorImpl.type = type;
		type.bind();
	}

	@Override
	public <T> void set(@NotNull Key<T> key, T value) {
		Validate.notNull(key, "key cannot be null!");
		if(!key.setState(value)) {
			this.map.put(key, value);
		}
	}

	@Override
	public <T> T get(@NotNull Key<T> key) {
		Validate.notNull(key, "key cannot be null!");
		T val = key.getState();
		return val != null ? val : (T) this.map.get(key);
	}

	@Override
	public boolean isPressed(ClickType type) {
		return this.pressed.test(type);
	}

	@Override
	public Cursor transformed(Transform2d transform) {
		Point2f p2f = transform.transform(this.x, this.y);
		return new CursorImpl(this.map, p2f.x(), p2f.y(), this.pressed);
	}
}
