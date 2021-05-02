package io.github.astrarre.gui.v0.rei;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import io.github.astrarre.gui.internal.rei.RecipeDisplayRootContainer;
import io.github.astrarre.gui.internal.rei.WidgetAdapter;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.rei.container.REICompatibleContainerGUI;
import io.github.astrarre.gui.v0.rei.container.REIContainerInitializer;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.gui.widget.Widget;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class REIContainerGUIRecipeCategory<T extends RecipeDisplay> implements RecipeCategory<T> {
	protected final EntryStack logo;
	protected final Identifier category;
	protected final REIContainerInitializer<T> function;
	protected String name;

	public REIContainerGUIRecipeCategory(EntryStack logo,
			Identifier category,
			REIContainerInitializer<T> function) {
		this.logo = logo;
		this.category = category;
		this.name = I18n.translate("recipe.category." + category.toString().replace(':', '.'));
		this.function = function;
	}

	@Override
	public @NotNull Identifier getIdentifier() {
		return this.category;
	}

	@Override
	public @NotNull String getCategoryName() {
		return this.name;
	}

	// todo clipping container that uses stencil to clip the container to the bounds
	@Override
	public @NotNull List<Widget> setupDisplay(T recipeDisplay, Rectangle bounds) {
		RecipeDisplayRootContainer container = new RecipeDisplayRootContainer(MinecraftClient.getInstance().currentScreen, bounds);
		WidgetAdapter adapter = new WidgetAdapter(container, bounds);
		REICompatibleContainerGUI<T> containerGUI = this.function.create(container, bounds.width, bounds.height);
		containerGUI.initRei(recipeDisplay);
		return Collections.singletonList(adapter);
	}

	@Override
	public @NotNull EntryStack getLogo() {
		return this.logo;
	}
}
