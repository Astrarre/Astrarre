package io.github.astrarre.access.v0.api.util;

import io.github.astrarre.access.v0.api.Provider;
import io.github.astrarre.access.v0.api.func.Access;
import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.v0.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityProvider {
	static <T> WorldFunction<T> getWorldFunction(Provider<? extends Access<T>, T> provider) {
		return (direction, state, view, pos, entity) -> {
			if(entity instanceof BlockEntityProvider) {
				return ((BlockEntityProvider) entity).get(provider, direction);
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
