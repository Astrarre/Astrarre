package io.github.astrarre.util.v0.fabric.fake_player;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class FakeServerPlayNetworkHandler extends ServerPlayNetworkHandler {
	public FakeServerPlayNetworkHandler(MinecraftServer server, ServerPlayerEntity player) {
		super(server, FakeClientConnection.INSTANCE, player);
	}
}
