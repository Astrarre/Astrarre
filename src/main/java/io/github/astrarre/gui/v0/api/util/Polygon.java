package io.github.astrarre.gui.v0.api.util;

import io.github.astrarre.common.internal.mixin.Matrix4fAccessor;
import io.github.astrarre.common.v0.api.Validate;
import io.github.astrarre.common.v0.api.util.math.Transformation;
import io.github.astrarre.gui.internal.util.MathUtil;
import io.github.astrarre.gui.internal.util.UnsafeFloatArrayList;

public final class Polygon {
	private final float[] vertices;
	private final int offset, length;

	public Polygon(float[] vertices) {
		this(vertices, 0, vertices.length);
	}

	/**
	 * @param vertices the vertices buffer [x1, y1, z1, x2, y2, z2, x3, y3, z3, etc.]
	 */
	public Polygon(float[] vertices, int offset, int length) {
		if (length % 3 != 0) {
			throw new IllegalArgumentException("verticies must be a multiple of three! [x1, y1, z1, x2, y2, z2, x3, y3, z3]");
		}

		Validate.positive(offset, "offset < 0!");
		Validate.positive(length, "length < 0!");
		Validate.greaterThanEqualTo(vertices.length, offset + length, "offset + length >= verticies.length!");

		this.offset = offset;
		this.length = length;
		this.vertices = vertices;
	}

	// todo transform by Transformation

	/**
	 * this builder can be re-used
	 * @implNote the polygon is passed the original length, and since we only add to the list, the old polygon should never be modified
	 */
	public static final class Builder {
		private final UnsafeFloatArrayList list;
		/**
		 * @param expectedVertices the number of expected vertices, if you don't know ahead of time, just guess
		 */
		public Builder(int expectedVertices) {
			this.list = new UnsafeFloatArrayList(expectedVertices * 3);
		}

		public Builder transform(Transformation transformation) {
			for (int i = 0; i < this.list.size(); i+=3) {
				// todo offset
				transformation.init();
				Matrix4fAccessor matrix = (Matrix4fAccessor) (Object) transformation.modelMatrixTransform;
				float f = this.list.getFloat(i);
				float g = this.list.getFloat(i + 1);
				float h = this.list.getFloat(i + 2);
				this.list.set(i, matrix.getA00() * f + matrix.getA01() * g + matrix.getA02() * h);
				this.list.set(i, matrix.getA10() * f + matrix.getA11() * g + matrix.getA12() * h);
				this.list.set(i, matrix.getA20() * f + matrix.getA21() * g + matrix.getA22() * h);
			}
			return this;
		}

		public Builder addVertex(float x, float y, float z) {
			// 3 points define a plane
			if (this.list.size() > 9) {
				Validate.isTrue(MathUtil.areCoplanar(this.list.getFloat(0),
						this.list.getFloat(1),
						this.list.getFloat(2),
						this.list.getFloat(3),
						this.list.getFloat(4),
						this.list.getFloat(5),
						this.list.getFloat(6),
						this.list.getFloat(7),
						this.list.getFloat(8),
						x,
						y,
						z), "(" + x + ", " + y + ", " + z + ")");
			}
			this.list.add(x);
			this.list.add(y);
			this.list.add(z);
			return this;
		}

		public Polygon build() {
			return new Polygon(this.list.arr(), 0, this.list.size());
		}
	}
}
