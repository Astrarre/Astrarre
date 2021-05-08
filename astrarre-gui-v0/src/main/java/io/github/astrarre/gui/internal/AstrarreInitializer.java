package io.github.astrarre.gui.internal;

import java.util.function.Consumer;

import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.mixin.ScreenHandlerTypeAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultHandledScreen;
import io.github.astrarre.gui.internal.vanilla.DefaultScreenHandler;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.util.v0.api.Id;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;


@Mod("astarre-gui-v9")
public class AstrarreInitializer {
	public static final Id CHANNEL = Id.create("astrarre-gui-v0", "open_screen_handler");
	public static final OpenScreenS2CPacket FAKE = new OpenScreenS2CPacket();
	public static final ScreenHandlerType<DefaultScreenHandler> PANEL_SCREEN =
			ScreenHandlerTypeAccess.createScreenHandlerType((syncId, inventory) -> new DefaultScreenHandler(syncId));

	public AstrarreInitializer() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(ScreenHandlerType.class, (Consumer<RegistryEvent.Register<ScreenHandlerType>>) event -> {
			PANEL_SCREEN.setRegistryName(new Identifier("astrarre-gui-v0", "default_screen"));
			event.getRegistry().register(PANEL_SCREEN);
		});
		ModPacketHandler.INSTANCE.registerSynchronizedClient(CHANNEL, (id, tag) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			PlayerEntity player = client.player;
			DefaultScreenHandler handler = PANEL_SCREEN.create(tag.getInt("syncId"), player.inventory);
			Screen screen = new DefaultHandledScreen(handler,
					player.inventory,
					FabricSerializers.TEXT.read(tag, "name"));
			((ScreenRootAccess)handler).readRoot(tag);
			player.currentScreenHandler = ((ScreenHandlerProvider<?>) screen).getScreenHandler();
			client.openScreen(screen);
		});
	}

}
