package io.github.astrarre.internal.rendering.mixin;

import io.github.astrarre.internal.rendering.access.Matrix4fAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.Matrix4f;

@Mixin(Matrix4f.class)
public class Matrix4fMixin implements Matrix4fAccess {
	@Shadow protected float a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33;

	/**
	 * @param x the origin in which to rotate the matrix
	 */
	@Override
	public void astrarre_rotate(float angle, float x, float y, float z) {
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float oneminusc = 1.0f - c;
		float xy = x*y;
		float yz = y*z;
		float xz = x*z;
		float xs = x*s;
		float ys = y*s;
		float zs = z*s;

		float f00 = x*x*oneminusc+c;
		float f01 = xy*oneminusc+zs;
		float f02 = xz*oneminusc-ys;
		// n[3] not used
		float f10 = xy*oneminusc-zs;
		float f11 = y*y*oneminusc+c;
		float f12 = yz*oneminusc+xs;
		// n[7] not used
		float f20 = xz*oneminusc+ys;
		float f21 = yz*oneminusc-xs;
		float f22 = z*z*oneminusc+c;

		float t00 = this.a00 * f00 + this.a10 * f01 + this.a20 * f02;
		float t01 = this.a01 * f00 + this.a11 * f01 + this.a21 * f02;
		float t02 = this.a02 * f00 + this.a12 * f01 + this.a22 * f02;
		float t03 = this.a03 * f00 + this.a13 * f01 + this.a23 * f02;
		float t10 = this.a00 * f10 + this.a10 * f11 + this.a20 * f12;
		float t11 = this.a01 * f10 + this.a11 * f11 + this.a21 * f12;
		float t12 = this.a02 * f10 + this.a12 * f11 + this.a22 * f12;
		float t13 = this.a03 * f10 + this.a13 * f11 + this.a23 * f12;
		this.a20 = this.a00 * f20 + this.a10 * f21 + this.a20 * f22;
		this.a21 = this.a01 * f20 + this.a11 * f21 + this.a21 * f22;
		this.a22 = this.a02 * f20 + this.a12 * f21 + this.a22 * f22;
		this.a23 = this.a03 * f20 + this.a13 * f21 + this.a23 * f22;
		this.a00 = t00;
		this.a01 = t01;
		this.a02 = t02;
		this.a03 = t03;
		this.a10 = t10;
		this.a11 = t11;
		this.a12 = t12;
		this.a13 = t13;
	}

	@Override
	public void astrarre_addToLastColumn(float x, float y, float z) {
		this.a03 += x;
		this.a13 += y;
		this.a23 += z;
	}
}
