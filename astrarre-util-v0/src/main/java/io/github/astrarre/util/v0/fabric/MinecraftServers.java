package io.github.astrarre.util.v0.fabric;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;

public class MinecraftServers {
	/**
	 * the currently active minecraft server (null if none active or on client)
	 */
	@Nullable
	public static MinecraftServer activeServer;
}
