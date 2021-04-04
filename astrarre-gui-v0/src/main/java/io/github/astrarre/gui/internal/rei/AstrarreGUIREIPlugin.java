package io.github.astrarre.gui.internal.rei;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultHandledScreen;
import io.github.astrarre.gui.internal.vanilla.DefaultScreen;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class AstrarreGUIREIPlugin implements REIPluginV0 {
	private static final Identifier PLUGIN_ID = new Identifier("astrarre-gui-v0", "rei_plugin");

	@Override
	public Identifier getPluginIdentifier() {
		return PLUGIN_ID;
	}

	@Override
	public void registerBounds(DisplayHelper displayHelper) {
		BaseBoundsHandler baseBoundsHandler = BaseBoundsHandler.getInstance();
		baseBoundsHandler.registerExclusionZones(DefaultScreen.class, () -> {
			DefaultScreen screen = (DefaultScreen) MinecraftClient.getInstance().currentScreen;
			return get(((ScreenRootAccess) screen).getClientRoot().getContentPanel()); // Create your list of rectangles somewhere!
		});
		baseBoundsHandler.registerExclusionZones(DefaultHandledScreen.class, () -> {
			DefaultHandledScreen screen = (DefaultHandledScreen) MinecraftClient.getInstance().currentScreen;
			return get(((ScreenRootAccess) screen).getRoot().getContentPanel()); // Create your list of rectangles somewhere!
		});
	}

	public static List<Rectangle> get(APanel rootPanel) {
		List<Rectangle> rectangles = new ArrayList<>();
		for (ADrawable drawable : rootPanel) {
			Polygon polygon = drawable.getBounds().toBuilder().transform(drawable.getTransformation()).build().getEnclosing();
			Rectangle rectangle = new Rectangle(
					(int) polygon.getX(0),
					(int) polygon.getY(0),
					(int) (polygon.getX(2) - polygon.getX(0)),
					(int) (polygon.getY(2) - polygon.getY(0)));
			if(rectangle.getWidth() > 0 && rectangle.getHeight() > 0) {
				rectangles.add(rectangle);
			}
		}
		return rectangles;
	}
}
