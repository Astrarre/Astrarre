package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultHandledScreen;
import io.github.astrarre.gui.internal.vanilla.DefaultScreenHandler;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class AstrarreInitializer implements ModInitializer {
	public static final ScreenHandlerType<DefaultScreenHandler> PANEL_SCREEN = ScreenHandlerRegistry.registerExtended(new Identifier(
			"astrarre-gui-v0",
			"default_screen"), (syncId, inventory, buf) -> {
		DefaultScreenHandler handler = new DefaultScreenHandler(syncId);
		((ScreenRootAccess)handler).readRoot(buf);
		return handler;
	});

	@Override
	public void onInitialize() {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ScreenRegistry.register(PANEL_SCREEN, DefaultHandledScreen::new);
		}
	}

}
