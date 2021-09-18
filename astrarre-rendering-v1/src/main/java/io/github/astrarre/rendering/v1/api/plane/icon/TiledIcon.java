package io.github.astrarre.rendering.v1.api.plane.icon;


import io.github.astrarre.rendering.v1.api.space.Render3d;

public record TiledIcon(Icon icon, int repeatX, int repeatY) implements Icon {
	@Override
	public float width() {
		return this.icon.width() * this.repeatX;
	}

	@Override
	public float height() {
		return this.icon.height() * this.repeatY;
	}

	@Override
	public void render(Render3d render) {
		if(this.icon instanceof FlatColorIcon f && f.offX() == 0 && f.offY() == 0) {
			render.fill().rect(f.argb(), 0, 0, f.width() * this.repeatX, f.height() * this.repeatY);
		} else {
			for(int x = 0; x < this.repeatX; x++) {
				for(int y = 0; y < this.repeatY; y++) {
					try(var ignore = render.translate(this.icon.width() * x, this.icon.height() * y)) {
						this.icon.render(render);
					}
				}
			}
		}
	}
}
