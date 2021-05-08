package io.github.astrarre.gui.internal;

import java.util.function.Consumer;

import io.github.astrarre.gui.internal.vanilla.DefaultHandledScreen;
import io.github.astrarre.gui.internal.vanilla.DefaultScreenHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;

@Mod("astrarre-gui-v0")
public class AstrarreInitializer {
	public static final ScreenHandlerType<DefaultScreenHandler> PANEL_SCREEN = new ScreenHandlerType<>((syncId, inventory) -> {
		DefaultScreenHandler handler = new DefaultScreenHandler(syncId);
		//todo ((ScreenRootAccess)handler).readRoot(buf);
		return handler;
	});

	public AstrarreInitializer() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(ScreenHandlerType.class, (Consumer<RegistryEvent.Register<ScreenHandlerType<?>>>) event -> {
			event.getRegistry().register(PANEL_SCREEN);
		});
		HandledScreens.register(PANEL_SCREEN, DefaultHandledScreen::new);
	}

}
