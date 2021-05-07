package io.github.astrarre.util.internal.server;

import io.github.astrarre.util.v0.api.common.MinecraftServer;

import net.minecraft.entity.player.Player;

public class ServerMinecraftServer implements MinecraftServer {
	protected final net.minecraft.server.MinecraftServer server;

	public ServerMinecraftServer(net.minecraft.server.MinecraftServer server) {
		this.server = server;
	}

	@Override
	public Iterable<Player> getPlayers() {
		return server.playerManager.players;
	}
}
