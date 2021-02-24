package io.github.astrarre.extensions.internal.astrarre;

import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.internal.mixin.Matrix4fAccessor;
import io.github.astrarre.v0.client.util.math.Vec3f;

public class Vec3fExtensions {
	public static void transform(Vec3f vec3f, Transformation transformation) {
		transformation.init();
		Matrix4fAccessor matrix = (Matrix4fAccessor) (Object) transformation.modelMatrixTransform;
		float f = vec3f.getX();
		float g = vec3f.getY();
		float h = vec3f.getZ();
		vec3f.set(matrix.getA00() * f + matrix.getA01() * g + matrix.getA02() * h,
				matrix.getA10() * f + matrix.getA11() * g + matrix.getA12() * h,
				matrix.getA20() * f + matrix.getA21() * g + matrix.getA22() * h);
	}
}