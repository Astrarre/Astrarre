package io.github.astrarre.common.v0.api;

import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.server.MinecraftServer;


public class Astrarre {
	private static MinecraftServer currentServer;

	public static MinecraftServer getCurrentServer() {
		if(currentServer == null) throw new IllegalStateException("No active server!");
		return currentServer;
	}

	@Hide
	public static net.minecraft.server.MinecraftServer getMinecraftServer() {
		return (net.minecraft.server.MinecraftServer) currentServer;
	}

	@Hide
	public static void setCurrentServer(MinecraftServer server) {
		currentServer = server;
	}
}
