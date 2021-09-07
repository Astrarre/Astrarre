package io.github.astrarre.rendering.internal;

import java.awt.geom.Point2D;

import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.Point2f;
import io.github.astrarre.rendering.v1.api.util.Point3D;
import io.github.astrarre.rendering.v1.api.util.Point3f;

import net.minecraft.util.math.Matrix4f;

// todo impl directly on Matrix4f for speed?
public record MatrixTransform3D(Matrix4f matrix) implements Transform3d {
	@Override
	public Transform3d invert() {
		Matrix4f inverted = new Matrix4f(this.matrix);
		inverted.invert();
		return new MatrixTransform3D(inverted);
	}

	@Override
	public float transformX(float x, float y, float z) {
		final Matrix4f matrix = this.matrix;
		return matrix.a00 * x + matrix.a01 * y + matrix.a02 * z + matrix.a03;
	}

	@Override
	public float transformY(float x, float y, float z) {
		final Matrix4f matrix = this.matrix;
		return matrix.a10 * x + matrix.a11 * y + matrix.a12 * z + matrix.a13;
	}

	@Override
	public float transformZ(float x, float y, float z) {
		final Matrix4f matrix = this.matrix;
		return matrix.a20 * x + matrix.a21 * y + matrix.a22 * z + matrix.a23;
	}

	@Override
	public void transform(Point2D point) {
		double x = point.getX(), y = point.getY();
		final Matrix4f matrix = this.matrix;
		point.setLocation(matrix.a00 * x + matrix.a01 * y + matrix.a02 + matrix.a03, matrix.a10 * x + matrix.a11 * y + matrix.a12 + matrix.a13);
	}

	@Override
	public Point2f transform(float x, float y) {
		return new Point2f(this.transformX(x, y), this.transformY(x, y));
	}

	@Override
	public void transform(Point3D point) {
		double x = point.getX(), y = point.getY(), z = point.getZ();
		final Matrix4f matrix = this.matrix;
		point.setLocation(matrix.a00 * x + matrix.a01 * y + matrix.a02 * z + matrix.a03,
		                  matrix.a10 * x + matrix.a11 * y + matrix.a12 * z + matrix.a13,
		                  matrix.a20 * x + matrix.a21 * y + matrix.a22 * z + matrix.a23);
	}

	@Override
	public Point3f transform(float x, float y, float z) {
		return new Point3f(this.transformX(x, y, z), this.transformY(x, y, z), this.transformZ(x, y, z));
	}

	@Override
	public Transform3d andThen(Transform3d transform) {
		if(transform instanceof MatrixTransform3D matrix) {
			Matrix4f copy = new Matrix4f(this.matrix);
			copy.multiply(matrix.matrix);
			return new MatrixTransform3D(copy);
		} else {
			return Transform3d.super.andThen(transform);
		}
	}

	@Override
	public Transform2d andThen(Transform2d transform) {
		if(transform instanceof MatrixTransform3D matrix) {
			Matrix4f copy = new Matrix4f(this.matrix);
			copy.multiply(matrix.matrix);
			return new MatrixTransform3D(copy);
		} else {
			return Transform3d.super.andThen(transform);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Translate: ")
				.append("[")
				.append(this.matrix.a30)
				.append(", ")
				.append(this.matrix.a31)
				.append(", ")
				.append(this.matrix.a32)
				.append("]\n");
		builder.append("Scale: ")
				.append("[")
				.append(this.matrix.a00)
				.append(", ")
				.append(this.matrix.a11)
				.append(", ")
				.append(this.matrix.a22)
				.append("]\n");
		builder.append("theta: ")
				.append(Math.acos(this.matrix.a22));
		return builder.toString();
	}
}
