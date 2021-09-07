package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.github.astrarre.gui.v1.api.listener.component.ResizeListener;
import io.github.astrarre.gui.v1.api.listener.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.util.Transformed;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An appendable group of components
 */
public class APanel extends AGroup {
	protected final List<Transformed<?>> cmps = new ArrayList<>();
	final ResizeListener listener = (width, height) -> this.recomputeBounds();
	float minX, minY;

	@NotNull
	@Override
	public Iterator<Transformed<?>> iterator() {
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

	public APanel add(Transformed<?>... component) {
		this.cmps.addAll(Arrays.asList(component));
		this.recomputeBounds();
		for(Transformed<?> transform : component) {
			transform.component().onResize.andThen(this.listener);
		}
		return this;
	}

	public APanel remove(Transformed<?>... component) {
		this.cmps.removeAll(Arrays.asList(component));
		for(Transformed<?> transform : component) {
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
		for(Transformed<?> t : this) {
			Transform3d tr = t.transform();
			AComponent c = t.component();
			Rect rect = bounds(tr, c.getWidth(), c.getHeight());
			maxX = Math.max(maxX, rect.maxX);
			maxY = Math.max(maxY, rect.maxY);
			minX = Math.min(minX, rect.minX);
			minY = Math.min(minY, rect.minY);
		}

		this.lockBounds(false);
		this.setBounds(maxX, maxY);
		this.lockBounds(true);
		this.minX = minX;
		this.minY = minY;
	}

	public record Rect(float minX, float minY, float maxX, float maxY) {}

	public interface CoordinateTransformer {
		float accept(Transform2d accept, float x, float y);
	}

	public static Rect bounds(Transform2d tr, float w, float h) {
		float mxx, mxy, mnx, mny;
		{
			float a = tr.transformX(w, h);
			float b = tr.transformX(0, h);
			float c = tr.transformX(w, 0);
			float d = tr.transformX(0, 0);
			mxx = Math.max(Math.max(a, b), Math.max(c, d));
			mnx = Math.min(Math.min(a, b), Math.min(c, d));
		}
		{
			float a = tr.transformY(w, h);
			float b = tr.transformY(0, h);
			float c = tr.transformY(w, 0);
			float d = tr.transformY(0, 0);
			mxy = Math.max(Math.max(a, b), Math.max(c, d));
			mny = Math.min(Math.min(a, b), Math.min(c, d));
		}

		return new Rect(mnx, mny, mxx, mxy);
	}

	public float getMinX() {
		return this.minX;
	}

	public float getMinY() {
		return this.minY;
	}
}
