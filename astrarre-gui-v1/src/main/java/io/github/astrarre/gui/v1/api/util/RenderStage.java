package io.github.astrarre.gui.v1.api.util;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import org.jetbrains.annotations.Nullable;

/**
 * Essentially this can be used to store render calls to be rendered later. It's a nice way of being able to render things from Interactable methods.
 */
public final class RenderStage implements GuiRenderable {
	final ConcurrentMap<String, Object> renderStates = new ConcurrentHashMap<>();

	/**
	 * puts a render stage that lasts for a given amount of time
	 */
	public RenderStage putTimed(String state, @Nullable GuiRenderable renderable, long ms) {
		if(renderable == null) {
			this.renderStates.remove(state);
		} else {
			this.renderStates.put(state, new Stage(renderable, System.currentTimeMillis() + ms));
		}
		return this;
	}

	public RenderStage put(String state, @Nullable GuiRenderable renderable) {
		if(renderable == null) {
			this.renderStates.remove(state);
		} else {
			this.renderStates.put(state, renderable);
		}
		return this;
	}

	@Override
	public void render(Cursor cursor, Render3d render) {
		Iterator<Object> iterator = this.renderStates.values().iterator();
		long current = System.currentTimeMillis();
		while(iterator.hasNext()) {
			Object to = iterator.next();
			GuiRenderable renderable;
			if(to instanceof Stage s) {
				if(s.expiry < current) {
					iterator.remove();
					continue;
				} else {
					renderable = s.renderable;
				}
			} else {
				renderable = (GuiRenderable) to;
			}

			renderable.render(cursor, render);
		}
	}

	record Stage(GuiRenderable renderable, long expiry) {}
}
