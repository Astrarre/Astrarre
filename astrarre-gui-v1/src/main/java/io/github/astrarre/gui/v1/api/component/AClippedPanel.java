package io.github.astrarre.gui.v1.api.component;

import java.util.Iterator;

import com.google.common.collect.Iterators;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.util.Transformed;
import io.github.astrarre.rendering.internal.Renderer2DImpl;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.edge.Stencil;
import org.jetbrains.annotations.NotNull;

/**
 * A panel that clips anything that's rendered outside it's bounds
 */
public class AClippedPanel extends APanel {
	final float width, height;
	final boolean boundsClipping;

	/**
	 * @param clipping if true, the clipped panel will attempt to cull components that are already outside its bounds
	 */
	public AClippedPanel(float width, float height, boolean clipping) {
		this.width = width;
		this.height = height;
		this.lockBounds(false);
		this.setBounds(width, height);
		this.lockBounds(true);
		this.boundsClipping = clipping;
	}

	@Override
	public @NotNull Iterator<Transformed<?>> iterator() {
		if(this.boundsClipping) {
			return Iterators.filter(super.iterator(), input -> {
				var c = input.component();
				Rect rect = bounds(input.transform(), c.getWidth(), c.getHeight());
				return !(rect.maxX() < 0 || rect.maxY() < 0 || rect.minX() > this.width || rect.minY() > this.height);
			});
		} else {
			return super.iterator();
		}
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		Stencil stencil = Renderer2DImpl.STENCIL;
		int id = stencil.startStencil(Stencil.Type.TRACING);
		render.fill().rect(0xffffffff, 0, 0, this.width, this.height);
		render.flush();
		stencil.fill(id);
		super.render0(cursor, render);
		stencil.endStencil(id);
	}

	@Override
	protected void recomputeBounds() {
	}
}
