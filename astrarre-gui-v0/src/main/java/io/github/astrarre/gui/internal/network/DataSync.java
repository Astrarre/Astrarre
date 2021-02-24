package io.github.astrarre.gui.internal.network;

import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.MinecraftClient;

public class DataSync {
	public static final int HUD = 0;
	public static final int ACTIVE_SCREEN = 2;

	static {
		ModPacketHandler.INSTANCE.registerClient(Id.newInstance("astrarre-gui-v0","sync"), (id, buf) -> {
			int index = buf.readUnsignedByte();
			if(index == HUD) {
				MinecraftClient client = MinecraftClient.getInstance();
			}
		});
	}
}
