package io.github.astrarre.gui.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.internal.access.PanelScreenAccess;
import io.github.astrarre.gui.internal.std.StandardHandledScreen;
import io.github.astrarre.gui.internal.std.StandardScreen;
import io.github.astrarre.gui.v1.api.component.APanel;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;

import net.minecraft.client.gui.screen.Screen;

public class AstrarreGuiReiPlugin implements REIClientPlugin {
	@Override
	public void registerExclusionZones(ExclusionZones zones) {
		zones.register(StandardHandledScreen.class, AstrarreGuiReiPlugin::provide);
		zones.register(StandardScreen.class, AstrarreGuiReiPlugin::provide);
	}

	static Collection<Rectangle> provide(Screen screen) {
		if(screen instanceof PanelScreenAccess access && access.hasRootPanel()) {
			APanel panel = access.getRootPanel();
			List<Rectangle> rectangles = new ArrayList<>();
			panel.find(null, transformed -> {
				var bounds = APanel.bounds(transformed);
				rectangles.add(new Rectangle((int)bounds.minX(), (int)bounds.minY(), (int)bounds.width(), (int)bounds.height()));
				return false;
			});
			return rectangles;
		}
		return Collections.emptyList();
	}
}
