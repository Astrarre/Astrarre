package io.github.astrarre.gui.v0.rei.container;

import io.github.astrarre.gui.v0.api.RootContainer;
import me.shedaniel.rei.api.RecipeDisplay;

public interface REIContainerInitializer<T extends RecipeDisplay> {
	REICompatibleContainerGUI<T> create(RootContainer container, int width, int height);
}
