package io.github.astrarre.v0.api.util;

import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.server.MinecraftServer;


public class Globals {
	private static MinecraftServer currentServer;

	public MinecraftServer getServer() {
		return currentServer;
	}

	@Hide
	public static void setCurrentServer(MinecraftServer server) {
		currentServer = server;
	}
}
