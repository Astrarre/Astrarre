package io.github.astrarre.rendering.v1.api.space;

import java.awt.geom.Point2D;

import io.github.astrarre.rendering.internal.MatrixTransform3D;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.rendering.v1.api.util.Point2f;
import io.github.astrarre.rendering.v1.api.util.Point3D;
import io.github.astrarre.rendering.v1.api.util.Point3f;

import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public interface Transform3d extends Transform2d {
	Transform3d IDENTITY = new MatrixTransform3D(new Matrix4f());

	static Transform3d translate(float x, float y, float z) {
		return new MatrixTransform3D(Matrix4f.translate(x, y, z));
	}

	static Transform3d scale(float scaleX, float scaleY, float scaleZ) {
		return new MatrixTransform3D(Matrix4f.scale(scaleX, scaleY, scaleZ));
	}

	static Transform3d rotate(float axisX, float axisY, float axisZ, AngleFormat format, float theta) {
		return new MatrixTransform3D(new Matrix4f(new Quaternion(new Vec3f(axisX, axisY, axisZ), format.convert(AngleFormat.RADIAN, theta), false)));
	}

	@Override
	Transform3d invert();

	float transformX(float x, float y, float z);
	float transformY(float x, float y, float z);
	float transformZ(float x, float y, float z);

	@Override
	default float transformX(float x, float y) {
		return this.transformX(x, y, 0);
	}

	@Override
	default float transformY(float x, float y) {
		return this.transformY(x, y, 0);
	}

	@Override
	default void transform(Point2D point) {
		point.setLocation(this.transformX((float)point.getX(), (float) point.getY()), this.transformY((float) point.getX(), (float) point.getY()));
	}

	@Override
	default Point2f transform(float x, float y) {
		return new Point2f(this.transformX(x, y), this.transformY(x, y));
	}

	void transform(Point3D point);

	Point3f transform(float x, float y, float z);

	default Point3f transform(Point3f point) {
		return this.transform(point.x(), point.y(), point.z());
	}

	default Transform3d andThen(Transform3d transform) {
		return new Transform3d() {
			@Override
			public float transformX(float x, float y, float z) {
				float newX = Transform3d.this.transformX(x, y, z), newY = Transform3d.this.transformY(x, y, z), newZ = Transform3d.this.transformZ(x, y, z);
				return transform.transformX(newX, newY, newZ);
			}

			@Override
			public float transformY(float x, float y, float z) {
				float newX = Transform3d.this.transformX(x, y, z), newY = Transform3d.this.transformY(x, y, z), newZ = Transform3d.this.transformZ(x, y, z);
				return transform.transformY(newX, newY, newZ);
			}

			@Override
			public float transformZ(float x, float y, float z) {
				float newX = Transform3d.this.transformX(x, y, z), newY = Transform3d.this.transformY(x, y, z), newZ = Transform3d.this.transformZ(x, y, z);
				return transform.transformZ(newX, newY, newZ);
			}

			@Override
			public void transform(Point3D point) {
				Transform3d.this.transform(point);
				transform.transform(point);
			}

			@Override
			public Point3f transform(float x, float y, float z) {
				float newX = Transform3d.this.transformX(x, y, z), newY = Transform3d.this.transformY(x, y, z), newZ = Transform3d.this.transformZ(x, y, z);
				return transform.transform(newX, newY, newZ);
			}

			@Override
			public Transform3d invert() {
				return transform.invert().andThen(Transform3d.this.invert());
			}
		};
	}
}
