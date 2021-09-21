package io.github.astrarre.components.v0.api.components.client;


import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;

public interface WorldKey {
	static WorldKey currentWorld() {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.world == null) {
			throw new IllegalStateException("not in a world!");
		} else if(client.isIntegratedServerRunning()) {
			IntegratedServer server = client.getServer();
			server.getSaveProperties();
			// todo use Level/SaveProperties
		}
		throw new UnsupportedOperationException("not yet created!");
	}
}
