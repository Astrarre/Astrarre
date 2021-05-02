package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class GUIPacketHandler {
	public static final Id DRAWABLE_PACKET_CHANNEL = Id.create("astrarre-gui-v0", "sync");
	public static final Id ADD_DRAWABLE = Id.create("astrarre-gui-v0", "add_drawable");
	public static final Id REMOVE_DRAWABLE = Id.create("astrarre-gui-v0", "remove_drawable");

	static {
		ModPacketHandler.INSTANCE.registerSynchronizedClient(ADD_DRAWABLE, (id, buf) -> {
			RootContainer.Type type = RootContainer.TYPE_SERIALIZER.read(buf, "type");
			RootContainerInternal internal = get(type);
			if (internal != null) {
				ADrawable drawable = internal.getSerializer().read(buf, "drawable");
				internal.addSynced(drawable);
				((DrawableInternal) drawable).onAdded(internal);
			}
		});

		ModPacketHandler.INSTANCE.registerSynchronizedClient(REMOVE_DRAWABLE, (id, buf) -> {
			RootContainer.Type type = RootContainer.TYPE_SERIALIZER.read(buf, "type");
			RootContainerInternal internal = get(type);
			if (internal != null) {
				int syncId = buf.getInt("syncId");
				internal.removeRoot(internal.forId(syncId));
			}
		});

		// Drawable#sendToClient
		ModPacketHandler.INSTANCE.registerSynchronizedClient(DRAWABLE_PACKET_CHANNEL, (id, buf) -> {
			int channel = buf.getInt("channel");
			RootContainer.Type type = RootContainer.TYPE_SERIALIZER.read(buf, "type");
			RootContainerInternal internal = get(type);
			if (internal != null) {
				ADrawable drawable = internal.forId(buf.getInt("syncId"));
				if (drawable != null) {
					((DrawableInternal) drawable).receiveFromServer(internal, channel, buf.getTag("payload"));
				}
			}
		});

		ModPacketHandler.INSTANCE.registerSynchronizedServer(DRAWABLE_PACKET_CHANNEL, (member, id, buf) -> {
			int channel = buf.getInt("channel");
			RootContainer.Type type = RootContainer.TYPE_SERIALIZER.read(buf, "type");
			int syncId = buf.getInt("syncId");
			switch (type) {
			case HUD:
				throw new UnsupportedOperationException("Serverside HUD not supported yet!");
			case SCREEN:
				ScreenHandler handler = ((ServerPlayerEntity) member).currentScreenHandler;
				RootContainerInternal internal = ((ScreenRootAccess) handler).getRoot();
				if (internal != null) {
					ADrawable drawable = internal.forId(syncId);
					if (drawable != null) {
						((DrawableInternal) drawable).receiveFromClient(internal, member, channel, buf.getTag("payload"));
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

	public static void sendToClients(RootContainer root, NBTagView tag, int channel, int syncId) {
		NetworkMember member = root.getViewer();
		if (member != null) {
			NBTagView.Builder builder = NBTagView.builder();
			builder.putInt("channel", channel);
			builder.putInt("type", root.getType().ordinal());
			builder.putInt("syncId", syncId);
			builder.putTag("payload", tag);
			member.send(GUIPacketHandler.DRAWABLE_PACKET_CHANNEL, builder);
		}
	}

	public static void sendToServer(RootContainer root, NBTagView tag, int channel, int id) {
		ModPacketHandler.INSTANCE.sendToServer(
				GUIPacketHandler.DRAWABLE_PACKET_CHANNEL,
				NBTagView.builder().putInt("channel", channel).putInt("syncId", id).putTag("payload", tag).putInt("type", root.getType().ordinal()));
	}

	public static void addDrawable(RootContainer container, NetworkMember member, ADrawable drawable) {
		NBTagView.Builder builder = NBTagView.builder().putInt("type", container.getType().ordinal());
		container.getSerializer().save(builder, "drawable", drawable);
		member.send(ADD_DRAWABLE, builder);
	}

	public static void removeDrawable(NetworkMember member, RootContainer container, int syncId) {
		NBTagView tag = NBTagView.builder()
				.putInt("syncId", syncId)
				.putInt("type", container.getType().ordinal());
		member.send(REMOVE_DRAWABLE, tag);
	}
}
