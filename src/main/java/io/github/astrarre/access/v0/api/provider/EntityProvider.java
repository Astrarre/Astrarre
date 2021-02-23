package io.github.astrarre.access.v0.api.provider;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.Returns;
import io.github.astrarre.v0.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * implement this on your Entity class, this adds a provider
 */
public interface EntityProvider {
	/**
	 * @param access the provider accessing this block
	 * @return the instance, or null
	 */
	@Nullable <T> T get(Access<? extends Returns<T>, T> access, Direction direction);

}
