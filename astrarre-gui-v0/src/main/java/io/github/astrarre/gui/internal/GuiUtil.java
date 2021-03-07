package io.github.astrarre.gui.internal;

import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;

public class GuiUtil {
	public static void write(Polygon polygon, Output output) {
		int vertices = polygon.vertices();
		output.writeInt(vertices);
		for (int i = 0; i < vertices; i++) {
			output.writeFloat(polygon.getX(i));
			output.writeFloat(polygon.getY(i));
		}
	}

	public static Polygon readPolygon(Input input) {
		int vertices = input.readInt();
		Polygon.Builder builder = new Polygon.Builder(vertices);
		for (int i = 0; i < vertices; i++) {
			builder.addVertex(input.readFloat(), input.readFloat());
		}
		return builder.build();
	}

	public static void write(Transformation transformation, Output output) {
		output.writeFloat(transformation.roll);
		output.writeFloat(transformation.pitch);
		output.writeFloat(transformation.yaw);
		output.writeFloat(transformation.offX);
		output.writeFloat(transformation.offY);
		output.writeFloat(transformation.offZ);
		output.writeFloat(transformation.scaleX);
		output.writeFloat(transformation.scaleY);
		output.writeFloat(transformation.scaleZ);
	}

	public static Transformation readTransformation(Input input) {
		return new Transformation(
				input.readFloat(),
				input.readFloat(),
				input.readFloat(),
				input.readFloat(),
				input.readFloat(),
				input.readFloat(),
				input.readFloat(),
				input.readFloat(),
				input.readFloat());
	}

	public static void setSyncId(DrawableInternal drawable, int syncId) {
		drawable.id = syncId;
	}
}
