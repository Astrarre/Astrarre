package io.github.astrarre.gui.internal.rei;

import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultHandledScreen;
import io.github.astrarre.gui.internal.vanilla.DefaultScreen;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.gui.widget.Widget;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class AstrarreGUIREIPlugin implements REIPluginV0 {
	private static final Identifier PLUGIN_ID = new Identifier("astrarre-gui-v0", "rei_plugin");

	@Override
	public Identifier getPluginIdentifier() {
		return PLUGIN_ID;
	}

	@Override
	public void registerBounds(DisplayHelper displayHelper) {
		displayHelper.registerProvider(new DisplayHelper.DisplayBoundsProvider<DefaultScreen>() {
			@Override
			public Rectangle getScreenBounds(DefaultScreen screen) {
				return get(screen);
			}

			@Override
			public ActionResult shouldScreenBeOverlayed(Class<?> screen) {
				return ActionResult.SUCCESS;
			}

			@Override
			public Class<?> getBaseSupportedClass() {
				return DefaultScreen.class;
			}

			@Override
			public float getPriority() {
				return 100f;
			}
		});
		displayHelper.registerProvider(new DisplayHelper.DisplayBoundsProvider<DefaultHandledScreen>() {
			@Override
			public Rectangle getScreenBounds(DefaultHandledScreen screen) {
				return get(screen);
			}

			@Override
			public Class<?> getBaseSupportedClass() {
				return DefaultHandledScreen.class;
			}

			@Override
			public float getPriority() {
				return 100f;
			}

			@Override
			public ActionResult shouldScreenBeOverlayed(Class<?> screen) {
				return ActionResult.SUCCESS;
			}
		});
	}

	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {

	}

	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
	}

	public static Rectangle get(Object screen) {
		APanel rootPanel = ((ScreenRootAccess)screen).getRoot().getContentPanel();
		float minX = 1_000_000, minY = 1_000_000, maxX = 0, maxY = 0;
		for (ADrawable drawable : rootPanel) {
			Polygon polygon = drawable.getBounds().toBuilder().transform(drawable.getTransformation()).build().getEnclosing();
			float dMinX = polygon.getX(0);
			float dMinY = polygon.getY(0);
			float dMaxX = polygon.getX(2);
			float dMaxY = polygon.getY(2);
			if(Math.abs(dMaxX - dMinX) < 0.001f || Math.abs(dMaxY - dMinY) < 0.001f) {
				continue;
			}
			if(dMinX < minX) minX = dMinX;
			if(dMinY < minY) minY = dMinY;
			if(dMaxX > maxX) maxX = dMaxX;
			if(dMaxY > maxY) maxY = dMaxY;
		}
		return new Rectangle((int)minX - 2, (int)minY - 2, (int)(maxX - minX) + 5, (int)(maxY - minY) + 5);
	}
}
