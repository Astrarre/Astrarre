package io.github.astrarre.access.v0.fabric.provider;

import io.github.astrarre.access.v0.api.Access;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * implement this on your Entity class, this adds a provider
 */
public interface EntityProvider {
	/**
	 * @param access the provider accessing this block
	 * @return the instance, or null
	 */
	@Nullable Object get(Access<?> access, Direction direction);

}
