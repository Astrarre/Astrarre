package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.github.astrarre.gui.v1.api.listener.component.ResizeListener;
import io.github.astrarre.gui.v1.api.listener.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.util.TransformedComponent;
import io.github.astrarre.rendering.v1.api.plane.icon.wrapper.TransformedIcon;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An appendable group of components
 */
public class APanel extends AGroup implements ToggleableComponent {
	protected final List<TransformedComponent> cmps = new ArrayList<>();
	final ResizeListener listener = (width, height) -> this.recomputeBounds();
	float minX, minY;

	@NotNull
	@Override
	public Iterator<TransformedComponent> iterator() {
		return this.isEnabled() ? this.cmps.iterator() : Collections.emptyIterator();
	}

	@Override
	public void able(boolean enabled) {
		ToggleableComponent.super.able(enabled);
		this.recomputeBounds();
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

	public APanel add(TransformedComponent... component) {
		this.cmps.addAll(Arrays.asList(component));
		this.recomputeBounds();
		for(TransformedComponent transform : component) {
			transform.component().onResize.andThen(this.listener);
		}
		return this;
	}

	public APanel remove(TransformedComponent... component) {
		this.cmps.removeAll(Arrays.asList(component));
		for(TransformedComponent transform : component) {
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
		for(TransformedComponent t : this) {
			TransformedIcon.Rect rect = bounds(t);
			maxX = Math.max(maxX, rect.maxX());
			maxY = Math.max(maxY, rect.maxY());
			minX = Math.min(minX, rect.minX());
			minY = Math.min(minY, rect.minY());
		}

		this.lockBounds(false);
		this.setBounds(maxX, maxY);
		this.lockBounds(true);
		this.minX = minX;
		this.minY = minY;
	}

	public static TransformedIcon.Rect bounds(TransformedComponent t) {
		Transform3d tr = t.transform();
		AComponent c = t.component();
		return TransformedIcon.bounds(tr, c.getWidth(), c.getHeight());
	}

	public float getMinX() {
		return this.minX;
	}

	public float getMinY() {
		return this.minY;
	}
}
