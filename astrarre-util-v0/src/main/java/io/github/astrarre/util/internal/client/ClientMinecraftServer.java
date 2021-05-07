package io.github.astrarre.util.internal.client;

import java.util.Collections;

import io.github.astrarre.util.v0.api.common.MinecraftServer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.Player;

public class ClientMinecraftServer implements MinecraftServer {
	protected final Minecraft minecraft;

	public ClientMinecraftServer(Minecraft minecraft) {
		this.minecraft = minecraft;
	}

	@Override
	public Iterable<Player> getPlayers() {
		return Collections.singletonList(this.minecraft.player);
	}
}
