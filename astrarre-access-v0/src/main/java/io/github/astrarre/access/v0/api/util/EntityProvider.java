package io.github.astrarre.access.v0.api.util;

import io.github.astrarre.access.v0.api.Provider;
import io.github.astrarre.access.v0.api.func.Access;
import io.github.astrarre.access.v0.api.func.EntityFunction;
import io.github.astrarre.v0.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * implement this on your Entity class, this adds a provider
 */
public interface EntityProvider {
	static <T> EntityFunction<T> getEntityFunction(Provider<? extends Access<T>, T> provider) {
		return (direction, entity) -> {
			if (entity instanceof EntityProvider) {
				return ((EntityProvider) entity).get(provider, direction);
			}
			return null;
		};
	}

	/**
	 * @param provider the provider accessing this block
	 * @return the instance, or null
	 */
	@Nullable <T> T get(Provider<? extends Access<T>, T> provider, Direction direction);

}
