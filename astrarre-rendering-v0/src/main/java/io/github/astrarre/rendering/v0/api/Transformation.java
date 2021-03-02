package io.github.astrarre.rendering.v0.api;

import io.github.astrarre.stripper.Hide;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

/**
 * To take full advantage of optimization capabilities, store these in static fields
 */
public final class Transformation {
	public static final Transformation EMPTY = Transformation.translate(0, 0, 0);
	public final float roll, pitch, yaw;
	public final float offX, offY, offZ;
	public final float scaleX, scaleY, scaleZ;
	@Hide
	public Matrix4f modelMatrixTransform;
	/**
	 * @see MatrixStack#scale(float, float, float)
	 */
	@Hide
	private Matrix3f scaleNormal;

	/**
	 * first scale, rotate, then translate
	 *
	 * rotation always rotates about the origin!
	 * @param pitch for guis yaw 'rolls' the graphics
	 */
	public Transformation(float roll, float pitch, float yaw, float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ) {
		this.roll = roll;
		this.pitch = pitch;
		this.yaw = yaw;
		this.offX = offX;
		this.offY = offY;
		this.offZ = offZ;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
	}

	public static Transformation rotate(float roll, float pitch, float yaw) {
		return new Transformation(roll, pitch, yaw, 0, 0, 0, 1, 1, 1);
	}

	public static Transformation translate(float x, float y, float z) {
		return new Transformation(0, 0, 0, x, y, z, 1, 1, 1);
	}

	public static Transformation scale(float xScale, float yScale, float zScale) {
		return new Transformation(0, 0, 0, 0, 0, 0, xScale, yScale, zScale);
	}

	/**
	 * The order in which combines are called does not impact the order of transformation. a single Transformation instance will always scale, rotate then translate
	 * @return a new transaction created by adding together rotation and offset, and multiplying the scales
	 */
	public Transformation combine(Transformation transformation) {
		return new Transformation(transformation.roll + this.roll,
				transformation.pitch + this.pitch,
				transformation.yaw + this.yaw,
				transformation.offX + this.offX,
				transformation.offY + this.offY,
				transformation.offZ + this.offZ,
				transformation.scaleX * this.scaleX,
				transformation.scaleY * this.scaleY,
				transformation.scaleZ * this.scaleZ);
	}

	@Hide
	public void init() {
		if (this.modelMatrixTransform == null) {
			this.initNormalScale();
			this.initRotateAndTranslate();
		}
	}

	@Hide
	public void apply(MatrixStack matricies) {
		this.init();

		MatrixStack.Entry stack = matricies.peek();
		stack.getModel().multiply(this.modelMatrixTransform);

		if (this.scaleNormal == null) {
			stack.getNormal().multiply(-1.0f);
		} else {
			stack.getNormal().multiply(this.scaleNormal);
		}
	}

	private void initNormalScale() {
		float x = this.scaleX, y = this.scaleY, z = this.scaleZ;
		if (x == y && y == z) {
			if (x > 0.0F) {
				return;
			}

			this.scaleNormal = null;
			return;
		}

		float f = 1.0F / x;
		float g = 1.0F / y;
		float h = 1.0F / z;
		float i = MathHelper.fastInverseCbrt(f * g * h);
		this.scaleNormal = Matrix3f.scale(i * f, i * g, i * h);
	}

	private void initRotateAndTranslate() {
		Matrix4f translateRotate = Matrix4f.translate(this.offX, this.offY, this.offZ);
		translateRotate.multiply(new Quaternion(this.roll, this.pitch, this.yaw, true));
		Matrix4f matrix4f = Matrix4f.scale(this.scaleX, this.scaleY, this.scaleZ);
		translateRotate.multiply(matrix4f);

		this.modelMatrixTransform = translateRotate;
	}
}
