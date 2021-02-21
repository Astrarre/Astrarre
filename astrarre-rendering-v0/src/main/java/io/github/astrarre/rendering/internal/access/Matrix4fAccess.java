package io.github.astrarre.rendering.internal.access;

import net.minecraft.util.math.Matrix4f;

public interface Matrix4fAccess {
	static Matrix4fAccess cast(Matrix4f matrix4f) {
		return (Matrix4fAccess) (Object) matrix4f;
	}
	void astrarre_rotate(float angle, float x, float y, float z);
	void astrarre_addToLastColumn(float x, float y, float z);
}
