package io.github.astrarre.rendering.v1.api.plane;

import java.awt.geom.Point2D;

import io.github.astrarre.rendering.v1.api.util.Point2f;

public interface Transform2D {
	float transformX(float x, float y);

	float transformY(float x, float y);

	void transform(Point2D point);

	Point2f transform(float x, float y);

	default Point2f transform(Point2f point2F) {
		return this.transform(point2F.x(), point2F.y());
	}

	default Transform2D andThen(Transform2D transform) {
		return new Transform2D() {
			@Override
			public float transformX(float x, float y) {
				float transformedX = Transform2D.this.transformX(x, y), transformedY = Transform2D.this.transformY(x, y);
				return transform.transformX(transformedX, transformedY);
			}

			@Override
			public float transformY(float x, float y) {
				float transformedX = Transform2D.this.transformX(x, y), transformedY = Transform2D.this.transformY(x, y);
				return transform.transformY(transformedX, transformedY);
			}

			@Override
			public void transform(Point2D point) {
				Transform2D.this.transform(point);
				transform.transform(point);
			}

			@Override
			public Point2f transform(float x, float y) {
				Point2D transform = new Point2D.Float(x, y);
				this.transform(transform);
				return new Point2f((float) transform.getX(), (float) transform.getY());
			}
		};
	}
}
