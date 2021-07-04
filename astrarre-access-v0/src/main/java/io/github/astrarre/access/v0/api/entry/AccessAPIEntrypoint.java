package io.github.astrarre.access.v0.api.entry;

import io.github.astrarre.access.v0.api.Access;

/**
 * This entrypoint exists solely to give access to {@link Access#ON_ACCESS_INIT} so you can register listeners
 * @deprecated {@link AccessInitEntrypoint}
 */
@Deprecated
public interface AccessAPIEntrypoint {
	/**
	 * @see Access#ON_ACCESS_INIT
	 * @deprecated {@link AccessInitEntrypoint}
	 */
	void onAccessAPIInit();
}
