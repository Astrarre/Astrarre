package io.github.astrarre.gui.v1.api.component;

import java.util.Arrays;

import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.util.ComponentTransform;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

public class ACenteringPanel extends APanel {
	final AComponent component;
	float offX, offY;

	public static ACenteringPanel center(APanel panel, float width, float height) {
		ACenteringPanel centeringPanel = new ACenteringPanel(panel, width, height);
		panel.add(centeringPanel);
		return centeringPanel;
	}

	public ACenteringPanel(AComponent component, float width, float height) {
		this.component = component;
		this.lockBounds(false);
		this.setBounds(width, height);
		this.recomputeOffset();
		this.onResize.andThen((w, h) -> this.recomputeOffset());
		component.onResize.andThen((w, h) -> this.recomputeOffset());
	}

	protected void recomputeOffset() {
		this.offX = (this.component.getWidth() - this.getWidth()) / 2;
		this.offY = (this.component.getHeight() - this.getHeight()) / 2;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		var transform = Transform3d.translate(this.offX, this.offY, 0);
		try(var ignore = render.transform(transform)) {
			super.render0(cursor.transformed(transform.invert()), render);
		}
	}

	@Override
	public boolean inBounds(float x, float y) {
		return super.inBounds(x - this.offX, y - this.offY);
	}

	@Override
	protected boolean cursor(Cursor cursor, CursorCallback consumer) {
		var transformed = cursor.transformed(Transform3d.translate(-this.offX, -this.offY, 0));
		return super.cursor(transformed, consumer);
	}

	@Override
	protected void recomputeBounds() {
	}

	@Override
	public APanel add(ComponentTransform<?>... component) {
		this.cmps.addAll(Arrays.asList(component));
		return this;
	}

	@Override
	public APanel remove(ComponentTransform<?>... component) {
		this.cmps.addAll(Arrays.asList(component));
		return this;
	}

	@Override
	public float getWidth() {
		return super.getWidth() + this.offX;
	}

	@Override
	public float getHeight() {
		return super.getHeight() + this.offY;
	}

	@Override
	public AComponent getAtRecursive(float x, float y) {
		return super.getAtRecursive(x - this.offX, y - this.offY);
	}
}
