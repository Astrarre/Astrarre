package io.github.astrarre.gui.v1.api;

import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.util.RenderStage;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.util.v0.api.SafeCloseable;
import io.github.astrarre.util.v0.api.Validate;

public abstract class AComponent {
	protected final RenderStage pre = new RenderStage(), post = new RenderStage();
	private Transform3d transform = Transform3d.IDENTITY;
	private float width, height;
	byte flags;

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

		if(this.transform != Transform3d.IDENTITY) {
			Cursor transformed = cursor.transformed(this.transform.invert());
			try(SafeCloseable ignored = render.transform(this.transform)) {
				this.pre.render(transformed, render);
				this.render0(transformed, render);
				if(state == 1) {
					this.onMouseEnter(transformed, render);
				} else if(state == 2) {
					this.onMouseExit(transformed, render);
				}
				this.post.render(transformed, render);
			}
		} else {
			this.pre.render(cursor, render);
			this.render0(cursor, render);
			if(state == 1) {
				this.onMouseEnter(cursor, render);
			} else if(state == 2) {
				this.onMouseExit(cursor, render);
			}
			this.post.render(cursor, render);
		}
	}

	public boolean inBounds(float x, float y) {
		return x >= 0 && y >= 0 && x < this.width && y < this.height;
	}

	protected abstract void render0(Cursor cursor, Render3d render);

	/**
	 * Set basic rectangular bounds, complex bounds can be achieved by overriding {@link #inBounds(float, float)}}
	 * @see #lockBounds()
	 */
	public void setBounds(float width, float height) {
		Validate.greaterThanEqualTo(0, this.flags & LOCK_BOUNDS, "Cannot change bounds on " + this);
		this.width = width;
		this.height = height;
	}

	/**
	 * @see MouseListener#isIn(Cursor)
	 * @see #inBounds(float, float)
	 */
	public final boolean isIn(Cursor cursor) {
		return (this.flags & LISTEN_GLOBAL) != 0 || this.inBounds(cursor.x(), cursor.y());
	}

	/**
	 * if the cursor was not {@link #isIn(Cursor)} before and now is, this event is fired in the {@link #render(Cursor, Render3d)} method.
	 * this is fired *after* {@link #render0(Cursor, Render3d)}, but before {@link #post}
	 */
	protected void onMouseEnter(Cursor cursor, Render3d render) {}

	/**
	 * if the cursor was {@link #isIn(Cursor)} before and now is not, this event is fired in the {@link #render(Cursor, Render3d)} method.
	 * this is fired *after* {@link #render0(Cursor, Render3d)}, but before {@link #post}
	 */
	protected void onMouseExit(Cursor cursor, Render3d render) {}

	/**
	 * @return true if in the last render call, the component was in the component (hovering over it)
	 */
	protected boolean wasCursorIn() {
		return (this.flags & WAS_IN) != 0;
	}

	/**
	 * appends a given transformation to the current transformation
	 */
	public AComponent addTransform(Transform3d transform) {
		Validate.greaterThanEqualTo(0, this.flags & LOCK_TRANSFORM, "Cannot change transform on " + this);
		if(this.transform == Transform3d.IDENTITY) {
			this.transform = transform;
		} else {
			this.transform = this.transform.andThen(transform);
		}
		return this;
	}

	/**
	 * prevents the transform of the component from changing
	 */
	protected void lockTransform() {
		this.flags |= LOCK_TRANSFORM;
	}

	/**
	 * prevents the bounds of the component from changing
	 */
	protected void lockBounds() {
		this.flags |= LOCK_BOUNDS;
	}

	protected void unlockBounds() {
		this.flags &= ~LOCK_BOUNDS;
	}

	protected void unlockTransform() {
		this.flags &= ~LOCK_TRANSFORM;
	}

	/**
	 * @param enable if set len true {@link AComponent#isIn(Cursor)} will always return true,
	 *      causing the component len receive all mouse events even if they are out of bounds
	 */
	protected void globalMouseListener(boolean enable) {
		if(enable) {
			this.flags |= LISTEN_GLOBAL;
		} else {
			this.flags &= ~LISTEN_GLOBAL;
		}
	}

	/**
	 * @see #flags
	 */
	public static final int LOCK_TRANSFORM = 1, LOCK_BOUNDS = 2, LISTEN_GLOBAL = 4, WAS_IN = 8, FOCUSED = 16;

	public final float localizeX(float x, float y) {
		return this.transform.invert().transformX(x, y);
	}

	public final float localizeY(float x, float y) {
		return this.transform.invert().transformY(x, y);
	}

	public final Transform3d getTransform() {
		return this.transform;
	}
}
