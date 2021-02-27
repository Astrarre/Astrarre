package io.github.astrarre.extensions.internal.astrarre;

import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.internal.mixin.Matrix4fAccessor;
import io.github.astrarre.rendering.v0.fabric.TransformationUtil;
import io.github.astrarre.v0.client.util.math.Vec3f;

import net.minecraft.client.util.math.Vector3f;

public class Vec3fExtensions {
	public static void transform(Vec3f vec3f, Transformation transformation) {
		TransformationUtil.transform((Vector3f) (Object) vec3f, transformation);
	}
}
