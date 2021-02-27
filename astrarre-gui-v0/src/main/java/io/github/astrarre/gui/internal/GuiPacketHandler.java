package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.internal.access.ContainerAccess;
import io.github.astrarre.gui.v0.api.Container;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class GuiPacketHandler {
	public static final Id OPEN_GUI = Id.newInstance("astrarre-gui-v0", "openGui");

	public static final Id DRAWABLE_PACKET_CHANNEL = Id.newInstance("astrarre-gui-v0", "sync");

	static {
		ModPacketHandler.INSTANCE.registerClient(OPEN_GUI, (id, buf) -> {

		});

		// Drawable#sendToClient
		ModPacketHandler.INSTANCE.registerClient(DRAWABLE_PACKET_CHANNEL, (id, buf) -> {
			int channel = buf.readInt();

			Container.Type type = buf.readEnum(Container.Type.class);

			MinecraftClient client = MinecraftClient.getInstance();
			ContainerInternal container = null;
			switch (type) {
			case HUD:
				container = ((ContainerAccess) client.inGameHud).getContainer();
				break;
			case SCREEN:
				Screen screen = client.currentScreen;
				if (screen == null) {
					return;
				}
				container = ((ContainerAccess) screen).getContainer();
				break;
			}

			Drawable drawable = container.forId(buf.readInt());
			if (drawable != null) {
				((DrawableInternal) drawable).receiveFromServer(channel, buf);
			}
		});
	}
}
