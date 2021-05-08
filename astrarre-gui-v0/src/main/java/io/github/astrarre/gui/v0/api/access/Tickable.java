package io.github.astrarre.gui.v0.api.access;

import io.github.astrarre.gui.v0.api.RootContainer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface Tickable {
	/**
	 * ticked only on the client
	 */
	@OnlyIn(Dist.CLIENT)
	void tick(RootContainer container);
}
