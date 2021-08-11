package io.github.astrarre.rendering.internal;

import java.awt.geom.Point2D;

import io.github.astrarre.rendering.v1.api.space.Transform3D;
import io.github.astrarre.rendering.v1.api.util.Point2f;
import io.github.astrarre.rendering.v1.api.util.Point3D;
import io.github.astrarre.rendering.v1.api.util.Point3f;

import net.minecraft.util.math.Matrix4f;

public record MatrixTransform3D(Matrix4f matrix) implements Transform3D {
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
}
