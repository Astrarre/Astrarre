package io.github.astrarre.gui.v0.api.components.button;

import io.github.astrarre.gui.v0.api.Graphics2d;
import io.github.astrarre.gui.v0.api.components.Widget;
import io.github.astrarre.gui.v0.api.cursor.Cursor;

public class ClientButton extends Widget {
	protected final ButtonTextures textures;
	protected boolean isHovering, isPressed;
	protected Runnable action;

	public ClientButton(ButtonTextures textures, float height, float width) {
		this.textures = textures;
		this.height = height;
		this.width = width;
	}

	@Override
	public void render(Graphics2d g2d, float tickDelta) {
		if (this.isPressed) {
			g2d.drawTexture(this.textures.press, this.textures.pressX1, this.textures.pressY1, this.textures.pressX2, this.textures.pressY2,0, 0);
		} else if (this.isHovering) {
			g2d.drawTexture(this.textures.hover, this.textures.hoverX1, this.textures.hoverY1, this.textures.hoverX2, this.textures.hoverY2,0, 0);
		} else {
			g2d.drawTexture(this.textures.normal, this.textures.normalX1, this.textures.normalY1, this.textures.normalX2, this.textures.normalY2,0, 0);
		}
	}

	@Override
	public boolean onMouseOver(float x, float y) {
		this.isHovering = true;
		return true;
	}

	@Override
	public void onMouseLeave() {
		this.isHovering = false;
		this.isPressed = false;
	}

	@Override
	public boolean onPress(Cursor cursor, int button) {
		this.isPressed = true;
		return true;
	}

	@Override
	public boolean onRelease(Cursor cursor, int button) {
		this.isPressed = false;
		if (this.action != null) {
			this.action.run();
		}
		return true;
	}

	public void registerAction(Runnable runnable) {
		if (this.action == null) {
			this.action = runnable;
		} else {
			Runnable curr = this.action;
			this.action = () -> {
				curr.run();
				runnable.run();
			};
		}
	}

}
