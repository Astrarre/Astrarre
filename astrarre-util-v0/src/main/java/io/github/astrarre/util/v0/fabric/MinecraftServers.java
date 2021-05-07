package io.github.astrarre.util.v0.fabric;

import io.github.astrarre.util.v0.api.common.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class MinecraftServers {
	/**
	 * the currently active minecraft server (null if none active or on client)
	 */
	@Nullable
	public static MinecraftServer activeServer;
}
