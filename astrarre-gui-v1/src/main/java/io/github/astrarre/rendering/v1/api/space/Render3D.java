package io.github.astrarre.rendering.v1.api.space;

import io.github.astrarre.rendering.v1.api.plane.Render2D;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.util.v0.api.Edge;
import io.github.astrarre.util.v0.api.SafeCloseable;

import net.minecraft.util.math.Direction;

public interface Render3D extends Render2D {
	SafeCloseable transform(Transform3D transform);

	SafeCloseable translate(float offX, float offY, float offZ);

	SafeCloseable scale(float scaleX, float scaleY, float scaleZ);

	@Override
	default SafeCloseable rotate(AngleFormat format, float roll) {
		return this.rotate(Direction.Axis.Z, format, roll);
	}

	/**
	 * rotate the renderer about a custom axis.
	 * @see #rotate(Direction.Axis, AngleFormat, float)
	 */
	SafeCloseable rotate(float axisX, float axisY, float axisZ, AngleFormat format, float theta);

	/**
	 * To make sense of rotations, imagine an airplane who's nose is coming out of the screen.
	 * Roll rotates the aircraft around, after a 90 degree rotation, looking up from the cockpit would give you a few of the horizon.
	 * Pitch rotates the aircraft up or down, so a 90 degree rotation would have the plane going straight up or straight down.
	 * Yaw rotates the aircraft side to side, so a 90 degree rotation would have the airplane turning left (though that's not how you're supposed to turn left in an aircraft)
	 */
	@Edge
	SafeCloseable rotate(Direction.Axis axis, AngleFormat format, float theta);

	@Override
	default SafeCloseable translate(float offX, float offY) {
		return this.translate(offX, offY, 1);
	}

	@Override
	default SafeCloseable scale(float scaleX, float scaleY) {
		return this.scale(scaleX, scaleY, 1);
	}

	@Override
	default void line(int color, float x1, float y1, float x2, float y2) {
		this.line(color, x1, y1, 1, x2, y2, 1);
	}

	@Override
	default void deltaLine(int color, float x1, float y1, float dX, float dY) {
		this.deltaLine(color, x1, y1, 1, dX, dY, 1);
	}

	void line(int color, float x1, float y1, float z1, float x2, float y2, float z2);

	default void deltaLine(int color, float x1, float y1, float z1, float dX, float dY, float dZ) {
		this.line(color, x1, y1, z1, x1 + dX, y1 + dY, z1 + dZ);
	}
}
