package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.github.astrarre.gui.v1.api.listener.component.ResizeListener;
import io.github.astrarre.gui.v1.api.listener.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.util.ComponentTransform;
import io.github.astrarre.gui.internal.TransformedComponentImpl;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class APanel extends AGroup {
	protected final List<ComponentTransform<?>> cmps = new ArrayList<>();
	final ResizeListener listener = (width, height) -> this.recomputeBounds();
	float minX, minY;

	@NotNull
	@Override
	public Iterator<ComponentTransform<?>> iterator() {
		return this.cmps.iterator();
	}

	@Override
	protected @Nullable AComponent before(AComponent component) {
		int index = this.indexOf(component);
		return index < 1 ? null : this.cmps.get(index - 1).component();
	}

	protected int indexOf(AComponent component) {
		int index = -1;
		for(int i = 0; i < this.cmps.size(); i++) {
			if(this.cmps.get(i).component() == component) {
				index = i;
				break;
			}
		}
		return index;
	}

	@Override
	protected @Nullable AComponent after(AComponent component) {
		int index = this.indexOf(component);
		return index == -1 || index == (this.cmps.size() - 1) ? null : this.cmps.get(index + 1).component();
	}

	public APanel add(ComponentTransform<?>... component) {
		this.cmps.addAll(Arrays.asList(component));
		this.recomputeBounds();
		for(ComponentTransform<?> transform : component) {
			transform.component().onResize.andThen(this.listener);
		}
		return this;
	}

	public APanel remove(ComponentTransform<?>... component) {
		this.cmps.removeAll(Arrays.asList(component));
		for(ComponentTransform<?> transform : component) {
			if(transform.component() == this.focused) {
				this.focused = null;
				this.next(FocusDirection.FORWARD);
			}
			transform.component().onResize.remove(this.listener);
		}
		this.recomputeBounds();
		return this;
	}
	
	protected void recomputeBounds() {
		float maxX = 0, maxY = 0, minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
		for(ComponentTransform<?> t : this) {
			Transform3d tr = t.transform();
			AComponent c = t.component();
			float w = c.getWidth(), h = c.getHeight();
			float x = max(Transform2d::transformX, tr, w, h);
			float y = max(Transform2d::transformY, tr, w, h);
			maxX = Math.max(maxX, x);
			maxY = Math.max(maxY, y);
			minX = Math.min(minX, x);
			minY = Math.min(minY, y);
		}

		this.lockBounds(false);
		this.setBounds(maxX, maxY);
		this.lockBounds(true);
		this.minX = minX;
		this.minY = minY;
	}

	public interface CoordinateTransformer {
		float accept(Transform2d accept, float x, float y);
	}

	public static float max(CoordinateTransformer t, Transform2d transform, float w, float h) {
		float a = t.accept(transform, w, h);
		float b = t.accept(transform, 0, h);
		float c = t.accept(transform, w, 0);
		float d = t.accept(transform, 0, 0);
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	public float getMinX() {
		return this.minX;
	}

	public float getMinY() {
		return this.minY;
	}
}
