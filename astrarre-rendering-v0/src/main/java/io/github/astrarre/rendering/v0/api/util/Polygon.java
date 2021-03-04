package io.github.astrarre.rendering.v0.api.util;

import io.github.astrarre.rendering.internal.util.MathUtil;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.util.v0.api.Validate;
import io.github.astrarre.util.v0.api.collection.UnsafeFloatArrayList;

import net.minecraft.client.util.math.Vector4f;

public final class Polygon {
	public static final float EPSILON = .0001f;
	public static final Polygon EMPTY = new Polygon.Builder(3)
			                                    .addVertex(0, 0)
			                                    .addVertex(0, EPSILON)
			                                    .addVertex(EPSILON, 0)
			                                    .build();

	private final float[] vertices;
	private final int offset, length;

	public Polygon(float[] vertices) {
		this(vertices, 0, vertices.length);
	}

	/**
	 * @param vertices the vertices buffer [x1, y1, x2, y2, x3, y3, etc.]
	 */
	public Polygon(float[] vertices, int offset, int length) {
		if (length % 2 != 0) {
			throw new IllegalArgumentException("verticies must be a multiple of three! [x1, y1, x2, y2, x3, y3]");
		}

		if (length < 6) {
			throw new IllegalArgumentException("< 3 points is not a polygon!");
		}

		Validate.positive(offset, "offset < 0!");
		Validate.positive(length, "length < 0!");
		Validate.greaterThanEqualTo(vertices.length, offset + length, "offset + length >= verticies.length!");

		this.offset = offset;
		this.length = length;
		this.vertices = vertices;
	}

	private float get(int vertex, int offset) {
		int index = (this.offset + vertex * 2) + offset;
		Validate.lessThan(index, this.length, index + " out of bounds!");
		return this.vertices[index];
	}


	public boolean isInside(float px, float py) {
		int count = 0;
		int vertices = this.vertices();
		for (int i = 0; i < vertices; i++) {
			int next = (i + 1) % vertices;
			float x = this.getX(i), y = this.getY(i), nx = this.getX(next), ny = this.getY(next);
			if (MathUtil.linesIntersect(x, y, nx, ny, px, py, 1_000_000, py)) {
				if (Math.abs(MathUtil.rot(x, y, px, py, nx, ny)) < EPSILON) {
					return MathUtil.onSegment(x, y, px, py, nx, ny);
				}
				count++;
			}
		}
		return (count % 2 == 1); // Same as (count%2 == 1)
	}


	public int vertices() {
		return this.length / 2;
	}

	public float getX(int vertex) {
		return this.get(vertex, 0);
	}

	public float getY(int vertex) {
		return this.get(vertex, 1);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (int i = 0; i < this.length; i += 3) {
			builder.append('\n').append('\t').append('[').append(String.format("%3.3f", this.vertices[i])).append(',')
					.append(String.format("%3.3f", this.vertices[i + 1])).append(',').append(String.format("%3.3f", this.vertices[i + 2]))
					.append(']');
		}
		builder.append(']');
		return builder.toString();
	}

	public Builder toBuilder() {
		return new Builder(new UnsafeFloatArrayList(this.vertices, this.offset, this.length));
	}

	public interface PointWalker {
		void accept(float x1, float y1, float x2, float y2);
	}

	public void walk(PointWalker walker) {
		int vertices = this.vertices();
		for (int i = 0; i < vertices; i++) {
			walker.accept(this.getX(i), this.getY(i), this.getX((i + 1) % vertices), this.getY((i + 1) % vertices));
		}
	}

	/**
	 * this builder can be re-used
	 *
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

		private Builder(UnsafeFloatArrayList arr) {
			this.list = arr;
		}

		public Builder transform(Transformation transformation) {
			Vector4f v4f = new Vector4f(0, 0, 0, 1);
			transformation.init();

			for (int i = 0; i < this.list.size(); i += 2) {
				float f = this.list.getFloat(i);
				float g = this.list.getFloat(i + 1);
				v4f.set(f, g, 1, 1);
				v4f.transform(transformation.modelMatrixTransform);
				this.list.set(i, v4f.getX());
				this.list.set(i+1, v4f.getY());
			}

			return this;
		}

		public Builder addVertex(float x, float y) {
			this.list.add(x);
			this.list.add(y);
			return this;
		}

		public Polygon build() {
			return new Polygon(this.list.arr(), 0, this.list.size());
		}
	}
}
