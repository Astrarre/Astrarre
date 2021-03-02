package io.github.astrarre.gui.v0.api;

import java.util.Iterator;

import io.github.astrarre.gui.v0.api.access.Container;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.stripper.Hide;
import org.jetbrains.annotations.NotNull;

/**
 * root container, this is not meant to be implemented. Astrarre implements it for Screen and HUD
 * todo allow adding components from the client
 */
public interface RootContainer {
	enum Type {
		HUD,
		SCREEN
	}

	Type getType();

	boolean isClient();

	Panel getContentPanel();

	/**
	 * if {@link #getType()} == {@link Type#SCREEN} The iterable will be empty if on the clientside
	 * if {@link #getType()} == {@link Type#HUD} and on the server, the iterable will just have the player with the hud
	 */
	@Hide
	Iterable<NetworkMember> getViewers();

	<T extends Drawable & Interactable> void setFocus(T drawable);

	/**
	 * @return true if the user is dragging their mouse (in hud this is always false)
	 */
	boolean isDragging();
}
