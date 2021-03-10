package io.github.astrarre.gui.v0.api.access;

import io.github.astrarre.gui.v0.api.RootContainer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Tickable {
	/**
	 * ticked only on the client
	 */
	@Environment(EnvType.CLIENT)
	void tick(RootContainer container);
}
