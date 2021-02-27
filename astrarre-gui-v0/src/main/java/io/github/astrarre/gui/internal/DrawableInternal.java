package io.github.astrarre.gui.internal;

import java.util.function.Consumer;

import io.github.astrarre.gui.v0.api.Container;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.ApiStatus;

public abstract class DrawableInternal {
	// internal methods
	int id;
	public final Container container;

	protected DrawableInternal(Container container) {
		this.container = container;
		this.id = ((ContainerInternal)container).addRoot((Drawable) this);
	}

	public int getId() {
		return this.id;
	}

	@ApiStatus.OverrideOnly
	protected void receiveFromServer(int channel, Input input) {
	}

	@ApiStatus.OverrideOnly
	protected void receiveFromClient(NetworkMember member, int channel, Input input) {
	}
}
