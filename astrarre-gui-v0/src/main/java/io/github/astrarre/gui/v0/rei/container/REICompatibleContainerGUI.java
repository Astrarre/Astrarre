package io.github.astrarre.gui.v0.rei.container;

import java.util.List;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.AAggregateDrawable;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.container.ContainerGUI;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Either;
import me.shedaniel.rei.api.RecipeDisplay;
import org.jetbrains.annotations.Nullable;

/**
 * This ContainerGUI allows you to use the same code for both REI and regular GUIs in the same bit of code
 */
public abstract class REICompatibleContainerGUI<T extends RecipeDisplay> extends ContainerGUI {

	/**
	 * regular gui constructor
	 */
	public REICompatibleContainerGUI(RootContainer container, NetworkMember member, int width, int height) {
		super(container, member, width, height);
	}

	/**
	 * REI constructor
	 */
	public REICompatibleContainerGUI(RootContainer container, int width, int height) {
		super(container, null, width, height);
	}

	public void initRei(T recipe) {
		this.addGui(this.container.getContentPanel(), this.width, this.height, Either.ofLeft(recipe));
	}

	@Override
	protected void addGui(AAggregateDrawable panel, int width, int height, List<ASlot> playerSlots) {
		this.addGui(panel, width, height, Either.ofRight(playerSlots));
	}

	/**
	 * @param context if {@link Either#hasLeft()} then the container is a REI recipe displaying GUI, else it's a normal gui that was opened
	 */
	protected abstract void addGui(AAggregateDrawable panel, int width, int height, Either<T, List<ASlot>> context);
}
