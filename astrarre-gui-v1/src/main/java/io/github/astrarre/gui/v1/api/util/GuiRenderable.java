package io.github.astrarre.gui.v1.api.util;

import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.rendering.v1.api.space.Render3d;

public interface GuiRenderable {
	void render(Cursor cursor, Render3d render);
}
