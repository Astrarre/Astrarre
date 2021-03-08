package io.github.astrarre.gui.v0.api.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Tickable {
	/**
	 * ticked only on the client
	 */
	@Environment(EnvType.CLIENT)
	void tick();
}
