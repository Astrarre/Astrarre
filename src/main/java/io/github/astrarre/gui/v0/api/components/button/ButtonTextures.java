package io.github.astrarre.gui.v0.api.components.button;

import io.github.astrarre.gui.v0.api.textures.Texture;
import io.github.astrarre.v0.util.Id;

import net.minecraft.util.Identifier;

/**
 * @see Builder
 */
public class ButtonTextures {
	private static final Id TEXTURE = Id.newInstance("textures/gui/container/beacon.png");
	private static final Texture BEACON_TEXTURE = new Texture(TEXTURE, 256, 256);
	public static final ButtonTextures BEACON_BUTTON = new Builder()
			.setNormal(BEACON_TEXTURE, 0, 219, 21, 240)
			.setHover(BEACON_TEXTURE, 66, 219,87, 240)
			.setPressed(BEACON_TEXTURE, 22, 219, 43, 240)
			.build();

	public final Texture hover, press, normal;
	public final int hoverX1, hoverY1, hoverX2, hoverY2, pressX1, pressY1, pressX2, pressY2, normalX1, normalY1, normalX2, normalY2;

	public ButtonTextures(Texture hover,
			Texture press,
			Texture normal,
			int hoverX1,
			int hoverY1,
			int hoverX2,
			int hoverY2,
			int pressX1,
			int pressY1,
			int pressX2,
			int pressY2,
			int normalX1,
			int normalY1,
			int normalX2,
			int normalY2) {
		this.hover = hover;
		this.press = press;
		this.normal = normal;
		this.hoverX1 = hoverX1;
		this.hoverY1 = hoverY1;
		this.hoverX2 = hoverX2;
		this.hoverY2 = hoverY2;
		this.pressX1 = pressX1;
		this.pressY1 = pressY1;
		this.pressX2 = pressX2;
		this.pressY2 = pressY2;
		this.normalX1 = normalX1;
		this.normalY1 = normalY1;
		this.normalX2 = normalX2;
		this.normalY2 = normalY2;
	}

	public static class Builder {
		public Texture hover, press, normal;
		public int hoverX1, hoverY1, hoverX2, hoverY2, pressX1, pressY1, pressX2, pressY2, normalX1, normalY1, normalX2, normalY2;
		
		public Builder setHover(Texture hover, int x1, int y1, int x2, int y2) {
			this.hover = hover;
			this.hoverX1 = x1;
			this.hoverY1 = y1;
			this.hoverX2 = x2;
			this.hoverY2 = y2;
			return this;
		}

		public Builder setPressed(Texture press, int x1, int y1, int x2, int y2) {
			this.press = press;
			this.pressX1 = x1;
			this.pressY1 = y1;
			this.pressX2 = x2;
			this.pressY2 = y2;
			return this;
		}

		public Builder setNormal(Texture normal, int x1, int y1, int x2, int y2) {
			this.normal = normal;
			this.normalX1 = x1;
			this.normalY1 = y1;
			this.normalX2 = x2;
			this.normalY2 = y2;
			return this;
		}

		public ButtonTextures build() {
			return new ButtonTextures(this.hover,
					this.press,
					this.normal,
					this.hoverX1,
					this.hoverY1,
					this.hoverX2,
					this.hoverY2,
					this.pressX1,
					this.pressY1,
					this.pressX2,
					this.pressY2,
					this.normalX1,
					this.normalY1,
					this.normalX2,
					this.normalY2);
		}
	}
}
