package io.github.astrarre.gui.v1.api.util;

import io.github.astrarre.gui.internal.TransformedComponentImpl;
import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

public interface TransformedComponent {
	AComponent component();

	Transform3d transform();

	default float localizeX(float x, float y) {
		return this.transform().invert().transformX(x, y);
	}

	default float localizeY(float x, float y) {
		return this.transform().invert().transformY(x, y);
	}

	default TransformedComponent with(Transform3d transform) {
		return new TransformedComponentImpl(transform, this.component());
	}

	default TransformedComponent and(Transform3d transform) {
		return this.with(this.transform().andThen(transform));
	}

	default TransformedComponent before(Transform3d transform) {
		return this.with(transform.andThen(this.transform()));
	}
}
