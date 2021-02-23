package io.github.astrarre.access.v0.api.provider;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.Returns;
import org.jetbrains.annotations.Nullable;

public interface Provider {
	/**
	 * @param access the provider accessing this object
	 */
	@Nullable <T> T get(Access<?, T> access);
}
