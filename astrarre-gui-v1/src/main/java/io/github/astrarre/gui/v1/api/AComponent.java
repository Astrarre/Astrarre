package io.github.astrarre.gui.v1.api;

import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.util.RenderStage;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.util.v0.api.SafeCloseable;
import io.github.astrarre.util.v0.api.Validate;

public abstract class AComponent {
	private static final int LOCK_TRANSFORM = 1, LOCK_BOUNDS = 2, LISTEN_GLOBAL = 4;
	// todo make interactable use access api
	// todo all mutable elements should be "lockable"
	protected final RenderStage pre = new RenderStage(), post = new RenderStage();
	private byte flags;
	private Transform3d transform = Transform3d.IDENTITY, transformInvert = Transform3d.IDENTITY;
	private float width, height;

	public final void render(Cursor cursor, Render3d render) {
		if(this.transform != null) {
			try(SafeCloseable ignored = render.transform(this.transform); Cursor transformed = cursor.transformed(this.transform.invert())) {
				this.pre.render(transformed, render);
				this.render0(transformed, render);
				this.post.render(transformed, render);
			}
		} else {
			this.pre.render(cursor, render);
			this.render0(cursor, render);
			this.post.render(cursor, render);
		}
	}

	public void addTransform(Transform3d transform) {
		Validate.greaterThanEqualTo(0, this.flags & LOCK_TRANSFORM, "Cannot change transform on " + this);
		if(this.transform == Transform3d.IDENTITY) {
			this.transform = transform;
		} else {
			this.transform = this.transform.andThen(transform);
			this.transformInvert = Transform3d.IDENTITY;
		}
	}

	/**
	 * this is what is used
	 */
	public boolean inBounds(float x, float y) {
		Transform3d form = this.getTransformInvert();
		float newX = form.transformX(x, y), newY = form.transformY(x, y);
		return newX >= 0 && newY >= 0 && newX < this.width && newY < this.height;
	}

	/**
	 * @see MouseListener#isIn(Cursor)
	 */
	public boolean isIn(Cursor cursor) {
		return (this.flags & LISTEN_GLOBAL) != 0 || this.inBounds(cursor.x(), cursor.y());
	}

	public void setBounds(float width, float height) {
		Validate.greaterThanEqualTo(0, this.flags & LOCK_BOUNDS, "Cannot change bounds on " + this);
		this.width = width;
		this.height = height;
	}

	protected abstract void render0(Cursor cursor, Render3d render);

	protected Transform3d getTransformInvert() {
		Transform3d invert = this.transformInvert;
		if(invert == Transform3d.IDENTITY) {
			this.transformInvert = invert = this.transform.invert();
		}
		return invert;
	}

	protected void lockTransform() {
		this.flags |= LOCK_TRANSFORM;
	}

	protected void lockBounds() {
		this.flags |= LOCK_BOUNDS;
	}

	/**
	 * @param enable if set to true {@link AComponent#isIn(Cursor)} will always return true,
	 *      causing the component to receive all mouse events even if they are out of bounds
	 */
	protected void globalMouseListener(boolean enable) {
		if(enable) {
			this.flags |= LISTEN_GLOBAL;
		} else {
			this.flags &= ~LISTEN_GLOBAL;
		}
	}
}
