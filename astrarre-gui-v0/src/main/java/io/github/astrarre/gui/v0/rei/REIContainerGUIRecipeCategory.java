package io.github.astrarre.gui.v0.rei;

import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.internal.rei.RecipeDisplayRootContainer;
import io.github.astrarre.gui.internal.rei.WidgetAdapter;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.gui.widget.Widget;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class REIContainerGUIRecipeCategory<T extends RecipeDisplay> implements RecipeCategory<T> {
	protected final EntryStack logo;
	protected final int width, height;
	protected final String categoryName;
	protected final Identifier category;

	public REIContainerGUIRecipeCategory(EntryStack logo, int width, int height, String name, Identifier category) {
		this.logo = logo;
		this.width = width;
		this.height = height;
		this.categoryName = name;
		this.category = category;
	}

	@Override
	public @NotNull Identifier getIdentifier() {
		return this.category;
	}

	@Override
	public @NotNull String getCategoryName() {
		return this.categoryName;
	}

	// todo clipping container that uses stencil to clip the container to the bounds
	@Override
	public @NotNull List<Widget> setupDisplay(T recipeDisplay, Rectangle bounds) {
		RecipeDisplayRootContainer container = new RecipeDisplayRootContainer(MinecraftClient.getInstance().currentScreen, bounds);
		WidgetAdapter adapter = new WidgetAdapter(container);
		return Collections.singletonList(adapter);
	}

	@Override
	public @NotNull EntryStack getLogo() {
		return this.logo;
	}

	@Override
	public int getDisplayHeight() {
		return this.height;
	}

	@Override
	public int getDisplayWidth(T display) {
		return this.width; // can technically maybe call this dynamically
	}
}
