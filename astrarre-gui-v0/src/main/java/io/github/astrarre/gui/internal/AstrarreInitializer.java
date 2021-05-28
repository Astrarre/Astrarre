package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.mixin.ScreenHandlerTypeAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultHandledScreen;
import io.github.astrarre.gui.internal.vanilla.DefaultScreenHandler;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class AstrarreInitializer implements ModInitializer {
	public static final Id CHANNEL = Id.create("astrarre-gui-v0", "open_screen_handler");
	public static final Packet<?> FAKE = new KeepAliveS2CPacket(Long.MIN_VALUE);
	public static final ScreenHandlerType<DefaultScreenHandler> PANEL_SCREEN =
			ScreenHandlerTypeAccess.createScreenHandlerType((syncId, inventory) -> new DefaultScreenHandler(syncId));

	@Override
	public void onInitialize() {
		Registry.register(Registry.SCREEN_HANDLER, new Identifier("astrarre-gui-v0", "default_screen"), PANEL_SCREEN);
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ModPacketHandler.INSTANCE.registerSynchronizedClient(CHANNEL, (id, tag) -> {
				MinecraftClient client = MinecraftClient.getInstance();
				PlayerEntity player = client.player;
				if(player == null) return;
				DefaultScreenHandler handler = PANEL_SCREEN.create(tag.getInt("syncId"), player.getInventory());
				Screen screen = new DefaultHandledScreen(handler,
						player.getInventory(),
						FabricSerializers.TEXT.read(tag, "name"));
				((ScreenRootAccess)handler).readRoot(tag);
				player.currentScreenHandler = ((ScreenHandlerProvider<?>) screen).getScreenHandler();
				client.openScreen(screen);
			});
		}
	}

}
