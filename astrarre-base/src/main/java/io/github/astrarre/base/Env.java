package io.github.astrarre.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class Env {
	/**
	 * true if the game was launched as a client
	 */
	public static final boolean IS_CLIENT;
	static {
		IS_CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}
}
