package io.github.astrarre.rendering.v1.api.space;

import io.github.astrarre.rendering.v1.api.plane.Transform2D;
import io.github.astrarre.rendering.v1.api.util.Point3D;
import io.github.astrarre.rendering.v1.api.util.Point3f;

public interface Transform3D extends Transform2D {
	float transformX(float x, float y, float z);
	float transformY(float x, float y, float z);
	float transformZ(float x, float y, float z);

	@Override
	default float transformX(float x, float y) {
		return this.transformX(x, y, 1);
	}

	@Override
	default float transformY(float x, float y) {
		return this.transformY(x, y, 1);
	}

	void transform(Point3D point);

	Point3f transform(float x, float y, float z);

	default Point3f transform(Point3f point) {
		return this.transform(point.x(), point.y(), point.z());
	}
}
