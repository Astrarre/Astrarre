package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class GuiPacketHandler {
	public static final Id OPEN_GUI = Id.create("astrarre-gui-v0", "open_gui");
	public static final Id DRAWABLE_PACKET_CHANNEL = Id.create("astrarre-gui-v0", "sync");
	public static final Id ADD_DRAWABLE = Id.create("astrarre-gui-v0", "add_drawable");

	static {
		ModPacketHandler.INSTANCE.registerClient(ADD_DRAWABLE, (id, buf) -> {
			RootContainer.Type type = buf.readEnum(RootContainer.Type.class);
			RootContainerInternal internal = get(type);
			if(internal != null) {
				Drawable drawable = Drawable.read(internal, buf);
				internal.addSynced(drawable);
			}
		});

		// Drawable#sendToClient
		ModPacketHandler.INSTANCE.registerClient(DRAWABLE_PACKET_CHANNEL, (id, buf) -> {
			int channel = buf.readInt();
			RootContainer.Type type = buf.readEnum(RootContainer.Type.class);
			RootContainerInternal container = get(type);
			if(container != null) {
				Drawable drawable = container.forId(buf.readInt());
				if (drawable != null) {
					((DrawableInternal) drawable).receiveFromServer(channel, buf);
				}
			}
		});

		ModPacketHandler.INSTANCE.registerServer(DRAWABLE_PACKET_CHANNEL, (member, id, buf) -> {
			int channel = buf.readInt();
			RootContainer.Type type = buf.readEnum(RootContainer.Type.class);
			int syncId = buf.readInt();
			switch (type) {
			case HUD:
				throw new UnsupportedOperationException("Serverside HUD not supported yet!");
			case SCREEN:
				ScreenHandler handler = ((ServerPlayerEntity)member).currentScreenHandler;
				RootContainerInternal internal = ((ScreenRootAccess)handler).getRoot();
				if(internal != null) {
					Drawable drawable = internal.forId(syncId);
					if(drawable != null) {
						((DrawableInternal)drawable).receiveFromClient(member, channel, buf);
					}
				}
			}
		});
	}

	@Nullable
	private static RootContainerInternal get(RootContainer.Type type) {
		MinecraftClient client = MinecraftClient.getInstance();
		RootContainerInternal container = null;
		switch (type) {
		case HUD:
			container = ((ScreenRootAccess) client.inGameHud).getRoot();
			break;
		case SCREEN:
			Screen screen = client.currentScreen;
			if (screen == null) {
				return null;
			}
			container = ((ScreenRootAccess) screen).getRoot();
			break;
		}
		return container;
	}
}
