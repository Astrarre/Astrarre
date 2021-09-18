package io.github.astrarre.gui.v1.api.server;

import io.github.astrarre.gui.internal.GuiInternal;
import io.github.astrarre.gui.internal.std.SingleUsePacket;
import io.github.astrarre.gui.internal.std.StandardScreenHandlerFactory;
import io.github.astrarre.gui.v1.api.AstrarreGui;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.util.v0.api.Edge;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ServerPanel {
	/**
	 * Opens a server & client gui (like most in-world containers).
	 * @param entity the player
	 * @param client the clientside initializer
	 * @param server the serverside initializer
	 */
	static void openHandled(PlayerEntity entity, ClientInit client, ServerInit server) {
		var type = entity.getClass();
		if(type == ClientPlayerEntity.class) { // skip fake players
			openClient((ClientPlayerEntity) entity, client);
		} else if(type == ServerPlayerEntity.class) { // skip fake players
			openServer((ServerPlayerEntity) entity, server);
		} else if(GuiInternal.POSSIBLE_FAKE_PLAYER_CLASSES.add(type)) {
			GuiInternal.LOGGER.warn("Detected Possible Fake Player Class " + type);
		}
	}
	
	static void openServer(ServerPlayerEntity entity, ServerInit init) {
		var comms = PacketHandler.player(GuiInternal.OPEN, entity);
		var packet = new SingleUsePacket();
		try {
			entity.openHandledScreen(StandardScreenHandlerFactory.INSTANCE).ifPresent(value -> {
				ScreenHandler h = entity.currentScreenHandler;
				comms.startQueue();
				init.onInit(comms, (ServerPanel) h);
				comms.sendInfo(packet, builder -> builder.putInt("id", h.syncId));
				comms.flushQueue();
				((ServerPanel) h).addCloseListener(comms::close);
			});
		} catch(Throwable e) {
			comms.close();
			throw Validate.rethrow(e);
		}
	}
	
	static void openClient(ClientPlayerEntity entity, ClientInit init) {
		var comms = PacketHandler.player(GuiInternal.OPEN, entity);
		var packet = new SingleUsePacket();
		comms.listen(packet, view -> {
			try {
				int syncId = view.getInt("id");
				MinecraftClient mc = MinecraftClient.getInstance();
				Screen screen = mc.currentScreen;
				ScreenHandler handler = screen instanceof HandledScreen<?> s ? s.getScreenHandler() : null;
				if(handler != null && handler.syncId == syncId) {
					ARootPanel panel = ARootPanel.getPanel(screen);
					comms.startQueue();
					init.onInit(comms, panel);
					comms.flushQueue();
					((ServerPanel) handler).addCloseListener(comms::close);
					AstrarreGui.AUDITORS.get().auditClient(comms, panel);
				} else {
					GuiInternal.LOGGER.warn("syncId does not match! Maybe the packets arrived in the wrong order?");
					comms.close();
				}
			} catch(Throwable e) {
				comms.close();
				throw Validate.rethrow(e);
			}
		});
	}

	@Edge
	ScreenHandler screenHandler();

	ServerPanel addTickListener(Runnable runnable);

	ServerPanel addCloseListener(Runnable runnable);

	interface ClientInit {
		void onInit(PacketHandler communication, ARootPanel panel);
	}

	/**
	 * @see SlotKey#sync(PacketHandler, ServerPanel)
	 */
	interface ServerInit {
		void onInit(PacketHandler communication, ServerPanel panel);
	}
}
