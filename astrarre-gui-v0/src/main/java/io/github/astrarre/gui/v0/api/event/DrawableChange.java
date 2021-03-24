package io.github.astrarre.gui.v0.api.event;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;

public interface DrawableChange {
	interface TransformationListener {
		void onTransformationChange(ADrawable drawable, Transformation old, Transformation current);
	}
	
	interface BoundsListener {
		void onBoundsChange(ADrawable drawable, Polygon old, Polygon current);
	}
}
