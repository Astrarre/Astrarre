package io.github.astrarre.gui.v0.api;

import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.util.math.Vec3f;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

/**
 * To take full advantage of optimization capabilities, store these in static fields
 */
public class Transformation {
	public final Vec3f rotation;
	public final Vec3f offset;
	public final Vec3f scale;
	private Matrix4f modelMatrixTransform;
	/**
	 * @see net.minecraft.client.util.math.MatrixStack#scale(float, float, float)
	 */
	private Matrix3f scaleNormal;

	/**
	 * @param rotation the rotation in degrees
	 * @param offset
	 * @param scale
	 */
	public Transformation(Vec3f rotation, Vec3f offset, Vec3f scale) {
		this.rotation = rotation;
		this.offset = offset;
		this.scale = scale;
	}

	@Hide
	public void apply(MatrixStack matricies) {
		if (this.modelMatrixTransform == null) {
			this.initNormalScale();
			this.initRotateAndTranslate();
		}

		MatrixStack.Entry stack = matricies.peek();
		stack.getModel().multiply(this.modelMatrixTransform);

		if (this.scaleNormal == null) {
			stack.getNormal().multiply(-1.0f);
		} else {
			stack.getNormal().multiply(this.scaleNormal);
		}
	}

	private void initNormalScale() {
		float x = this.scale.getX(), y = this.scale.getY(), z = this.scale.getZ();
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
		float f = this.rotation.getX();
		float g = this.rotation.getY();
		float h = this.rotation.getZ();

		int leftM = 1;

		Matrix4f translateRotate = Matrix4f.translate(this.offset.getX() * leftM, this.offset.getY(), this.offset.getZ());

		translateRotate.multiply(new Quaternion(f, g * leftM, h * leftM, true));

		Matrix4f matrix4f = Matrix4f.scale(this.scale.getX(), this.scale.getY(), this.scale.getZ());
		translateRotate.multiply(matrix4f);

		this.modelMatrixTransform = translateRotate;
	}
}
