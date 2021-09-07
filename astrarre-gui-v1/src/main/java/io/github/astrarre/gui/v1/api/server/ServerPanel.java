package io.github.astrarre.gui.v1.api.server;

import io.github.astrarre.gui.internal.GuiInternal;
import io.github.astrarre.gui.internal.std.OpenGuiPacket;
import io.github.astrarre.gui.internal.std.StandardScreenHandlerFactory;
import io.github.astrarre.gui.v1.api.AstrarreGui;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.slot.ASlotHelper;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public interface ServerPanel {

	static void openHandled(PlayerEntity entity, ClientInit init, ServerInit server) {
		openHandled(entity, init, server, true);
	}

	/**
	 * Opens a server & client gui (like most in-world containers).
	 * @param entity the player
	 * @param client the clientside initializer
	 * @param server the serverside initializer
	 * @param audit whether to run post-process checks to catch common errors
	 */
	static void openHandled(PlayerEntity entity, ClientInit client, ServerInit server, boolean audit) {
		var comms = PacketHandler.player(GuiInternal.SERVER, entity);
		OpenGuiPacket packet = new OpenGuiPacket();

		if(entity.world.isClient) {
			// we need to wait until desktop hand is opened, hmm
			comms.listen(packet, view -> {
				try {
					int syncId = view.getInt("id");
					MinecraftClient mc = MinecraftClient.getInstance();
					Screen screen = mc.currentScreen;
					ScreenHandler handler = screen instanceof HandledScreen<?> s ? s.getScreenHandler() : null;
					if(handler != null && handler.syncId == syncId) {
						ARootPanel panel = ARootPanel.getPanel(screen);
						client.onInit(comms, panel);
						((ServerPanel) handler).addCloseListener(comms::close);
						if(audit) {
							AstrarreGui.AUDITORS.get().auditClient(comms, panel);
						}
					} else {
						GuiInternal.LOGGER.warn("syncId does not match! Maybe the packets arrived in the wrong order?");
						comms.close();
					}
				} catch(Throwable e) {
					comms.close();
					throw Validate.rethrow(e);
				}
			});
		} else {
			try {
				entity.openHandledScreen(StandardScreenHandlerFactory.INSTANCE).ifPresent(value -> {
					ScreenHandler h = entity.currentScreenHandler;
					server.onInit(comms, (ServerPanel) h);
					comms.sendInfo(packet, builder -> builder.putInt("id", h.syncId));
					((ServerPanel) h).addCloseListener(comms::close);
				});
			} catch(Throwable e) {
				comms.close();
				throw Validate.rethrow(e);
			}
		}
	}

	ScreenHandler screenHandler();

	ServerPanel addTickListener(Runnable runnable);

	ServerPanel addCloseListener(Runnable runnable);

	interface ClientInit {
		void onInit(PacketHandler communication, ARootPanel panel);
	}

	/**
	 * @see ASlotHelper#linkFromServer(PacketHandler, ServerPanel, SlotKey)
	 */
	interface ServerInit {
		void onInit(PacketHandler communication, ServerPanel panel);
	}


}
