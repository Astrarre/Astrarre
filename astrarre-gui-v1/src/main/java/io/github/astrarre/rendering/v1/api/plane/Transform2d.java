package io.github.astrarre.rendering.v1.api.plane;

import java.awt.geom.Point2D;

import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.Point2f;

public interface Transform2d {
	float transformX(float x, float y);

	float transformY(float x, float y);

	void transform(Point2D point);

	Point2f transform(float x, float y);

	Transform2d invert();

	default Point2f transform(Point2f point2F) {
		return this.transform(point2F.x(), point2F.y());
	}

	default Transform2d andThen(Transform2d transform) {
		return new Transform2d() {
			@Override
			public float transformX(float x, float y) {
				float transformedX = Transform2d.this.transformX(x, y), transformedY = Transform2d.this.transformY(x, y);
				return transform.transformX(transformedX, transformedY);
			}

			@Override
			public float transformY(float x, float y) {
				float transformedX = Transform2d.this.transformX(x, y), transformedY = Transform2d.this.transformY(x, y);
				return transform.transformY(transformedX, transformedY);
			}

			@Override
			public void transform(Point2D point) {
				Transform2d.this.transform(point);
				transform.transform(point);
			}

			@Override
			public Point2f transform(float x, float y) {
				Point2D transform = new Point2D.Float(x, y);
				this.transform(transform);
				return new Point2f((float) transform.getX(), (float) transform.getY());
			}

			@Override
			public Transform2d invert() {
				return transform.invert().andThen(Transform2d.this.invert());
			}
		};
	}
}
