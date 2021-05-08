package io.github.astrarre.gui.v0.api;

import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Axis2d;
import io.github.astrarre.rendering.v0.api.util.Vec2f;

public class AlignmentUtil {
	/**
	 * aligns the drawables to one axis one after the other
	 * @param seperation the space between each drawable
	 */
	public static void sequential(Axis2d axis, float seperation, ADrawable... drawables) {
		float counter = 0;
		for (ADrawable drawable : drawables) {
			Vec2f vec2f = axis.inDirection(counter);
			drawable.setTransformation(drawable.getTransformation());
			counter += drawable.getBounds().getEnclosing().get(axis, 2);
			counter += seperation;
		}
	}
}
