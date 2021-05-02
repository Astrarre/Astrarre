package io.github.astrarre.gui.internal.rei;

import io.github.astrarre.gui.internal.containers.ScreenRootContainer;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.access.Interactable;
import me.shedaniel.math.Rectangle;

import net.minecraft.client.gui.screen.Screen;

public class RecipeDisplayRootContainer extends ScreenRootContainer<Screen> {
	protected final Rectangle rectangle;

	public RecipeDisplayRootContainer(Screen screen, Rectangle rectangle) {
		super(screen);
		this.rectangle = rectangle;
	}

	@Override
	public Type getType() {
		return Type.REI_RECIPE;
	}

	@Override
	public <T extends ADrawable & Interactable> void setFocus(T drawable) {
		// todo
	}

	@Override
	public void addResizeListener(OnResize resize) {
		// todo
	}

	@Override
	public int getWidth() {
		return this.rectangle.width;
	}

	@Override
	public int getHeight() {
		return this.rectangle.height;
	}
}
