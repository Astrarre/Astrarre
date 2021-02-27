package io.github.astrarre.gui.v0.api;

import io.github.astrarre.gui.v0.api.bounds.Interactable;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.stripper.Hide;

/**
 * root container, this is not meant to be implemented. Astrarre implements it for Screen and HUD
 * todo allow adding components from the client
 */
public interface Container {
	enum Type {
		HUD,
		SCREEN
	}

	Type getType();

	boolean isClient();

	/**
	 * adds a component to draw to the container (also adds it to root)
	 */
	void add(Drawable drawable);

	/**
	 * if {@link #getType()} == {@link Type#SCREEN} The iterable will be empty if on the clientside
	 * if {@link #getType()} == {@link Type#HUD} and on the server, the iterable will just have the player with the hud
	 */
	@Hide
	Iterable<NetworkMember> getViewers();

	boolean setFocus(Interactable drawable);

	/**
	 * @return true if the user is dragging their mouse (in hud this is always false)
	 */
	boolean isDragging();
}
