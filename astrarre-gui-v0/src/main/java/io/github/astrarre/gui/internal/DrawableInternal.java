package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import org.jetbrains.annotations.ApiStatus;

public abstract class DrawableInternal {
	// internal methods
	int id;
	public final RootContainer rootContainer;

	protected DrawableInternal(RootContainer rootContainer) {
		this.rootContainer = rootContainer;
		this.id = ((RootContainerInternal) rootContainer).addRoot((Drawable) this);
	}

	public int getSyncId() {
		return this.id;
	}

	@ApiStatus.OverrideOnly
	protected void receiveFromServer(int channel, Input input) {
	}

	@ApiStatus.OverrideOnly
	protected void receiveFromClient(NetworkMember member, int channel, Input input) {
	}
}
