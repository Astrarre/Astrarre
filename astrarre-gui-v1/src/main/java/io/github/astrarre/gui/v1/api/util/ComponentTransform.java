package io.github.astrarre.gui.v1.api.util;

import io.github.astrarre.gui.internal.TransformedComponentImpl;
import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

public interface ComponentTransform<T extends AComponent> {
	T component();

	Transform3d transform();

	default float localizeX(float x, float y) {
		return this.transform().invert().transformX(x, y);
	}

	default float localizeY(float x, float y) {
		return this.transform().invert().transformY(x, y);
	}

	default ComponentTransform<T> with(Transform3d transform) {
		return new TransformedComponentImpl<>(transform, this.component());
	}

	default ComponentTransform<T> and(Transform3d transform) {
		return this.with(this.transform().andThen(transform));
	}

	default ComponentTransform<T> before(Transform3d transform) {
		return this.with(transform.andThen(this.transform()));
	}
}
