package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.util.ComponentTransform;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

public record TransformedComponentImpl<T extends AComponent>(Transform3d transform, T component) implements ComponentTransform<T> {
}
