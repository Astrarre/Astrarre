package io.github.astrarre.gui.v1.edge;

import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.rendering.internal.Renderer3DImpl;
import io.github.astrarre.rendering.v1.api.space.Render3d;

import net.minecraft.client.gui.Drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DrawableComponent extends AComponent {
	public final Drawable drawable;

	DrawableComponent(Drawable drawable) {
		this.drawable = drawable;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		this.drawable.render(((Renderer3DImpl)render).stack, (int) cursor.x(), (int) cursor.y(), 0);
	}
}
