package io.github.astrarre.gui.v0.api.access;

import io.github.astrarre.gui.v0.api.Drawable;
import org.jetbrains.annotations.Nullable;

/**
 * delegates and containers should implement this interface
 */
public interface Container extends Iterable<Drawable> {
	@Nullable
	<T extends Drawable & Interactable> T drawableAt(double x, double y);

	// todo bounds view n stuff
}
