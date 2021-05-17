package io.github.astrarre.rendering.v0.api;


import java.nio.IntBuffer;

import io.github.astrarre.itemview.v0.api.Serializable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

/**
 * To take full advantage of optimization capabilities, store these in static fields
 */
public final class Transformation implements Serializable {
	public static final Serializer<Transformation> SERIALIZER = Serializer.of(Transformation::new);

	public static final Transformation EMPTY = Transformation.translate(0, 0, 0);
	private final float roll, pitch, yaw;
	private final float offX, offY, offZ;
	private final float scaleX, scaleY, scaleZ;
	private Matrix4f modelMatrixTransform;
	/**
	 * @see MatrixStack#scale(float, float, float)
	 */
	private Matrix3f scaleNormal;

	/**
	 * first scale, rotate, then translate
	 *
	 * rotation always rotates about the origin!
	 *
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

	protected Transformation(NbtValue value) {
		IntList list = value.asIntList();
		this.roll = Float.intBitsToFloat(list.getInt(0));
		this.pitch = Float.intBitsToFloat(list.getInt(1));
		this.yaw = Float.intBitsToFloat(list.getInt(2));
		this.offX = Float.intBitsToFloat(list.getInt(3));
		this.offY = Float.intBitsToFloat(list.getInt(4));
		this.offZ = Float.intBitsToFloat(list.getInt(5));
		this.scaleX = Float.intBitsToFloat(list.getInt(6));
		this.scaleY = Float.intBitsToFloat(list.getInt(7));
		this.scaleZ = Float.intBitsToFloat(list.getInt(8));
	}

	@Override
	public NbtValue save() {
		IntList list = new IntArrayList();
		list.add(Float.floatToIntBits(this.roll));
		list.add(Float.floatToIntBits(this.pitch));
		list.add(Float.floatToIntBits(this.yaw));
		list.add(Float.floatToIntBits(this.offX));
		list.add(Float.floatToIntBits(this.offY));
		list.add(Float.floatToIntBits(this.offZ));
		list.add(Float.floatToIntBits(this.scaleX));
		list.add(Float.floatToIntBits(this.scaleY));
		list.add(Float.floatToIntBits(this.scaleZ));
		return NbtValue.of(NBTType.INT_ARRAY, list);
	}

	/**
	 * the rotation in degrees.
	 *
	 * (for guis, the Z axis is pointing out of the screen, so to rotate something in a gui, you should use yaw)
	 */
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
	 * The order in which combines are called does not impact the order of transformation. a single Transformation instance will always scale, rotate
	 * then translate
	 *
	 * @return a new transaction created by adding together rotation and offset, and multiplying the scales
	 */
	public Transformation combine(Transformation transformation) {
		return new Transformation(
				transformation.roll + this.roll,
				transformation.pitch + this.pitch,
				transformation.yaw + this.yaw,
				transformation.offX + this.offX,
				transformation.offY + this.offY,
				transformation.offZ + this.offZ,
				transformation.scaleX * this.scaleX,
				transformation.scaleY * this.scaleY,
				transformation.scaleZ * this.scaleZ);
	}

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

	private void init() {
		if (this.modelMatrixTransform == null) {
			this.initNormalScale();
			this.initRotateAndTranslate();
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

	public Matrix4f getModelMatrixTransform() {
		this.init();
		return this.modelMatrixTransform;
	}

	public Matrix3f getScaleNormal() {
		this.init();
		return this.scaleNormal;
	}

	@Override
	public int hashCode() {
		int result = (this.roll != +0.0f ? Float.floatToIntBits(this.roll) : 0);
		result = 31 * result + (this.pitch != +0.0f ? Float.floatToIntBits(this.pitch) : 0);
		result = 31 * result + (this.yaw != +0.0f ? Float.floatToIntBits(this.yaw) : 0);
		result = 31 * result + (this.offX != +0.0f ? Float.floatToIntBits(this.offX) : 0);
		result = 31 * result + (this.offY != +0.0f ? Float.floatToIntBits(this.offY) : 0);
		result = 31 * result + (this.offZ != +0.0f ? Float.floatToIntBits(this.offZ) : 0);
		result = 31 * result + (this.scaleX != +0.0f ? Float.floatToIntBits(this.scaleX) : 0);
		result = 31 * result + (this.scaleY != +0.0f ? Float.floatToIntBits(this.scaleY) : 0);
		result = 31 * result + (this.scaleZ != +0.0f ? Float.floatToIntBits(this.scaleZ) : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Transformation)) {
			return false;
		}

		Transformation that = (Transformation) o;

		if (Float.compare(that.roll, this.roll) != 0) {
			return false;
		}
		if (Float.compare(that.pitch, this.pitch) != 0) {
			return false;
		}
		if (Float.compare(that.yaw, this.yaw) != 0) {
			return false;
		}
		if (Float.compare(that.offX, this.offX) != 0) {
			return false;
		}
		if (Float.compare(that.offY, this.offY) != 0) {
			return false;
		}
		if (Float.compare(that.offZ, this.offZ) != 0) {
			return false;
		}
		if (Float.compare(that.scaleX, this.scaleX) != 0) {
			return false;
		}
		if (Float.compare(that.scaleY, this.scaleY) != 0) {
			return false;
		}
		return Float.compare(that.scaleZ, this.scaleZ) == 0;
	}

	public float getRoll() {
		return this.roll;
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getOffX() {
		return this.offX;
	}

	public float getOffY() {
		return this.offY;
	}

	public float getOffZ() {
		return this.offZ;
	}

	public float getScaleX() {
		return this.scaleX;
	}

	public float getScaleY() {
		return this.scaleY;
	}

	public float getScaleZ() {
		return this.scaleZ;
	}
}
