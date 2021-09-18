package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.gui.v1.api.listener.component.ResizeListener;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.util.RenderStage;
import io.github.astrarre.gui.v1.api.util.Transformed;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.util.v0.api.Validate;

public abstract class AComponent implements Transformed<AComponent> {
	/**
	 * @see #flags
	 */
	public static final int BACKGROUND = 1, LOCK_BOUNDS = 2, LISTEN_GLOBAL = 4, WAS_IN = 8, FOCUSED = 16, DISABLED = 32, SKIP_MOUSE_EVENT = 64;

	// todo give id or smth
	public final Access<ResizeListener> onResize = new Access<>("astrarre", "resize", array -> (w, h) -> {
		for(ResizeListener listener : array) {
			listener.onResize(w, h);
		}
	});
	protected final RenderStage pre = new RenderStage(), post = new RenderStage();
	byte flags;
	private float width, height;

	public final void render(Cursor cursor, Render3d render) {
		boolean isIn = this.isIn(cursor), wasIn = (this.flags & WAS_IN) != 0;
		int state = 0;
		if(isIn && !wasIn) {
			state = 1; // enter
			this.flags |= WAS_IN;
		} else if(!isIn && wasIn) {
			state = 2; // exit
			this.flags &= ~WAS_IN;
		}

		this.pre.render(cursor, render);
		this.render0(cursor, render);
		if(state == 1) {
			this.onMouseEnter(cursor, render);
		} else if(state == 2) {
			this.onMouseExit(cursor, render);
		}
		this.post.render(cursor, render);
	}

	public boolean inBounds(float x, float y) {
		return x >= 0 && y >= 0 && x < this.width && y < this.height;
	}

	/**
	 * Set basic rectangular bounds, complex bounds can be achieved by overriding {@link #inBounds(float, float)}}
	 *
	 * @see #lockBounds(boolean)
	 */
	public void setBounds(float width, float height) {
		Validate.greaterThanEqualTo(0, this.flags & LOCK_BOUNDS, "Cannot change bounds on " + this);
		float oldWidth = this.width, oldHeight = this.height;
		this.width = width;
		this.height = height;
		if(width != oldWidth || height != oldHeight) {
			this.onResize.get().onResize(width, height);
		}
	}

	/**
	 * @see MouseListener#isIn(Cursor)
	 * @see #inBounds(float, float)
	 */
	public final boolean isIn(Cursor cursor) {
		return (this.flags & LISTEN_GLOBAL) != 0 || this.inBounds(cursor.x(), cursor.y());
	}

	public boolean areBoundsLocked() {
		return this.is(LOCK_BOUNDS);
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}

	@Override
	public final AComponent component() {
		return this;
	}

	@Override
	public final Transform3d transform() {
		return this.transform0();
	}

	Transform3d transform0() {
		return Transform3d.IDENTITY;
	}

	protected abstract void render0(Cursor cursor, Render3d render);

	/**
	 * if the cursor was not {@link #isIn(Cursor)} before and now is, this event is fired in the {@link #render(Cursor, Render3d)} method. this is
	 * fired *after* {@link #render0(Cursor, Render3d)}, but before {@link #post}
	 */
	protected void onMouseEnter(Cursor cursor, Render3d render) {}

	/**
	 * if the cursor was {@link #isIn(Cursor)} before and now is not, this event is fired in the {@link #render(Cursor, Render3d)} method. this is
	 * fired *after* {@link #render0(Cursor, Render3d)}, but before {@link #post}
	 */
	protected void onMouseExit(Cursor cursor, Render3d render) {}

	/**
	 * @return true if in the last render call, the component was in the component (hovering over it)
	 */
	protected boolean wasCursorIn() {
		return (this.flags & WAS_IN) != 0;
	}

	/**
	 * prevents the bounds of the component from changing
	 */
	protected void lockBounds(boolean locked) {
		this.set(LOCK_BOUNDS, locked);
	}

	/**
	 * @param enable if set len true {@link AComponent#isIn(Cursor)} will always return true, causing the component len receive all mouse events
	 * 		even if they are out of bounds
	 */
	protected void globalMouseListener(boolean enable) {
		this.set(LISTEN_GLOBAL, enable);
	}

	boolean is(int flag) {
		return (this.flags & flag) != 0;
	}

	void set(int flag, boolean value) {
		if(value) {
			this.flags |= flag;
		} else {
			this.flags &= ~flag;
		}
	}
}
