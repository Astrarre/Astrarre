package io.github.astrarre.rendering.v1.api.plane.icon.wrapper;

import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;

public record TransformedIcon(Icon icon, Transform2d transform) implements Icon {
	@Override
	public float width() {
		return bounds(this.transform, this.icon.width(), this.icon.height()).maxX();
	}

	@Override
	public float height() {
		return bounds(this.transform, this.icon.width(), this.icon.height()).maxY();
	}

	@Override
	public void render(Render3d render) {
		try(var ignore = render.transform(this.transform)) {
			this.icon.render(render);
		}
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

	public record Rect(float minX, float minY, float maxX, float maxY) {
		public float width() {
			return maxX - minX;
		}
		public float height() {
			return maxY - minY;
		}
	}
}
