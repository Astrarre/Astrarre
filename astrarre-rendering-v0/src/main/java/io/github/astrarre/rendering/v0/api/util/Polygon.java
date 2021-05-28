package io.github.astrarre.rendering.v0.api.util;

import java.util.function.UnaryOperator;

import earcut4j.Earcut;
import io.github.astrarre.itemview.v0.api.Serializable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.rendering.internal.util.MathUtil;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.util.v0.api.Validate;
import io.github.astrarre.util.v0.api.collection.UnsafeFloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class Polygon implements Serializable {
	public static final Serializer<Polygon> SERIALIZER = Serializer.of(Polygon::new);

	public static final float EPSILON = .0001f;
	public static final Polygon EMPTY = new Polygon.Builder(3).addVertex(0, 0).addVertex(0, EPSILON).addVertex(EPSILON, 0).build();
	private final float[] vertices;
	private final int offset, length;
	private IntList triangulation;
	private Polygon enclosing;

	private Polygon(float[] vertices) {
		this(vertices, 0, vertices.length);
	}

	/**
	 * @param vertices the vertices buffer [x1, y1, x2, y2, x3, y3, etc.]
	 */
	private Polygon(float[] vertices, int offset, int length) {
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

	private Polygon(NbtValue value) {
		IntList list = value.asIntList();
		this.offset = 0;
		float[] vertices = this.vertices = new float[list.size()];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = Float.intBitsToFloat(list.getInt(i));
		}
		this.length = vertices.length;
	}

	/**
	 * @return a new regular polygon centered at [0, 0] for the given radii
	 */
	public static Polygon regular(int vertices, float radii) {
		Polygon.Builder builder = new Builder(vertices);
		for (int i = 0; i < vertices; i++) {
			double angle = 2 * Math.PI * i / vertices;
			double x = radii * Math.cos(angle), y = radii * Math.sin(angle);
			builder.addVertex((float) x, (float) y);
		}
		return builder.build();
	}

	public int indexOf(float px, float py) {
		for (int i = 0; i < this.vertices(); i++) {
			if (this.getX(i) == px && this.getY(i) == py) {
				return i;
			}
		}
		return -1;
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

	private float get(int vertex, int offset) {
		int index = (this.offset + vertex * 2) + offset;
		Validate.lessThan(index, this.length, index + " out of bounds!");
		return this.vertices[index];
	}

	public Polygon getEnclosing() {
		Polygon enclosing = this.enclosing;
		if (enclosing == null) {
			float maxX = 0, maxY = 0, minY = Float.MAX_VALUE, minX = Float.MAX_VALUE;
			for (int i = 0; i < this.vertices(); i++) {
				float x = this.getX(i), y = this.getY(i);
				if (x > maxX) {
					maxX = x;
				}
				if (y > maxY) {
					maxY = y;
				}
				if(x < minX) {
					minX = x;
				}
				if(y < minY) {
					minY = y;
				}
			}
			return this.enclosing = Polygon.rectangle(minX, minY, maxX, maxY);
		}
		return enclosing;
	}

	public static Polygon rectangle(float width, float height) {
		return rectangle(0, 0, width, height);
	}

	public static Polygon rectangle(float minX, float minY, float maxX, float maxY) {
		float[] buf = {
				minX,
				minY,
				minX,
				maxY,
				maxX,
				maxY,
				maxX,
				minY
		};
		Polygon polygon = new Polygon(buf);
		polygon.enclosing = polygon;
		return polygon;
	}

	public Builder toBuilder() {
		return new Builder(new UnsafeFloatArrayList(this.vertices, this.offset, this.length));
	}

	public void walk(PointWalker walker) {
		int vertices = this.vertices();
		for (int i = 0; i < vertices; i++) {
			walker.accept(this.getX(i), this.getY(i), this.getX((i + 1) % vertices), this.getY((i + 1) % vertices));
		}
	}

	@Override
	public int hashCode() {
		int hash = 1;
		for (int i = 0; i < this.length; i++) {
			hash = 31 * hash + Float.floatToIntBits(this.vertices[this.offset + i]);
		}
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Polygon)) {
			return false;
		}

		Polygon polygon = (Polygon) o;
		if (polygon.vertices() != this.vertices()) {
			return false;
		}

		int startIndex = polygon.indexOf(this.getX(0), this.getY(0));
		int vertices = this.vertices();
		for (int i = 0; i < vertices; i++) {
			if (this.getX(i) != polygon.getX((startIndex + i) % vertices) || this.getY(i) != polygon.getY((startIndex + i) % vertices)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (int i = 0; i < this.length; i += 2) {
			builder.append('\n').append('\t').append('[').append(String.format("%3.3f", this.vertices[i])).append(',')
					.append(String.format("%3.3f", this.vertices[i + 1])).append(']');
		}
		builder.append(']');
		return builder.toString();
	}

	@Override
	public NbtValue save() {
		IntList list = new IntArrayList();
		for (int i = 0; i < this.length; i++) {
			list.add(Float.floatToIntBits(this.vertices[i + this.offset]));
		}
		return NbtValue.of(NBTType.INT_ARRAY, list);
	}

	/**
	 * @deprecated internal
	 */
	@Deprecated
	@Environment (EnvType.CLIENT)
	public BufferBuilder triangleBuffer(MatrixStack stack, VertexFormat format, UnaryOperator<VertexConsumer> consumer) {
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(VertexFormat.DrawMode.TRIANGLES, format);
		IntList triangles = this.triangulate();
		for (int i = triangles.size() - 1; i >= 0; i--) {
			int vertex = triangles.getInt(i);
			consumer.apply(buffer.vertex(stack.peek().getModel(), this.getX(vertex), this.getY(vertex), 0)).next();
		}
		buffer.end();
		return buffer;
	}

	/**
	 * @return a list of 3 vertex indices of the triangles that compose this polygon
	 */
	public IntList triangulate() {
		IntList triangulation = this.triangulation;
		if (triangulation == null) {
			IntList list = Earcut.earcut(this.vertices, this.offset, this.length, null, 2);
			((IntArrayList) list).trim();
			return this.triangulation = IntLists.unmodifiable(list);
		}
		return triangulation;
	}

	public float get(Axis2d axis, int vertex) {
		if(axis.isX()) {
			return this.getX(vertex);
		} else {
			return this.getY(vertex);
		}
	}


	public interface PointWalker {
		void accept(float x1, float y1, float x2, float y2);
	}

	/**
	 * this builder cannot be re-used
	 *
	 * @implNote the polygon is passed the original length, and since we only add to the list, the old polygon should never be modified
	 */
	public static final class Builder {
		private UnsafeFloatArrayList list;

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
			for (int i = 0; i < this.list.size(); i += 2) {
				float f = this.list.getFloat(i);
				float g = this.list.getFloat(i + 1);
				v4f.set(f, g, 1, 1);
				v4f.transform(transformation.getModelMatrixTransform());
				this.list.set(i, v4f.getX());
				this.list.set(i + 1, v4f.getY());
			}
			return this;
		}

		public Builder addVertex(float x, float y) {
			this.list.add(x);
			this.list.add(y);
			return this;
		}

		/**
		 * @return a new polygon, with the points in counter-clockwise order
		 */
		public Polygon build() {
			Polygon polygon = new Polygon(this.list.arr(), 0, this.list.size());
			this.list = null;
			return polygon;
		}
	}
}
