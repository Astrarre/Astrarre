package io.github.astrarre.rendering.v0.fabric;

import io.github.astrarre.rendering.internal.mixin.Matrix4fAccessor;
import io.github.astrarre.rendering.v0.api.Transformation;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix4f;

public class TransformationUtil {
	public static void transform(Vector3f vec3f, Transformation transformation) {
		Matrix4fAccessor matrix = (Matrix4fAccessor) (Object) transformation.getModelMatrixTransform();
		float f = vec3f.getX();
		float g = vec3f.getY();
		float h = vec3f.getZ();
		vec3f.set(matrix.getA00() * f + matrix.getA01() * g + matrix.getA02() * h,
				matrix.getA10() * f + matrix.getA11() * g + matrix.getA12() * h,
				matrix.getA20() * f + matrix.getA21() * g + matrix.getA22() * h);
	}

	public static void transformInverse(Vector3f vec3f, Transformation transformation) {
		Matrix4f m4f = transformation.getModelMatrixTransform().copy();
		m4f.invert();
		Matrix4fAccessor matrix = (Matrix4fAccessor) (Object) m4f;
		float f = vec3f.getX();
		float g = vec3f.getY();
		float h = vec3f.getZ();
		vec3f.set(matrix.getA00() * f + matrix.getA01() * g + matrix.getA02() * h,
				matrix.getA10() * f + matrix.getA11() * g + matrix.getA12() * h,
				matrix.getA20() * f + matrix.getA21() * g + matrix.getA22() * h);
	}
}
