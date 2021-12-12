package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.util.TransformedComponent;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

public record TransformedComponentImpl(Transform3d transform, AComponent component) implements TransformedComponent {
}
