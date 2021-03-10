package io.github.astrarre.gui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import org.jetbrains.annotations.ApiStatus;

public abstract class DrawableInternal {

	public static final int UNINITIALIZED_ID = -1;
	// internal members
	int id = -1;

	List<RootContainer> rootsInternal = new ArrayList<>();
	public List<RootContainer> roots = Collections.unmodifiableList(this.rootsInternal);

	private boolean isClient;

	protected DrawableInternal() {
		this.isClient = IS_CLIENT.get();
	}

	public int getSyncId() {
		return this.id;
	}

	@ApiStatus.OverrideOnly
	protected void receiveFromServer(RootContainer container, int channel, Input input) {
	}

	@ApiStatus.OverrideOnly
	protected void receiveFromClient(RootContainer container, NetworkMember member, int channel, Input input) {
	}

	@ApiStatus.OverrideOnly
	protected void onAdded(RootContainer container) {
	}

	public boolean isClient() {
		return this.isClient;
	}

	void setClient(boolean client) {
		if (this.roots.isEmpty()) {
			throw new IllegalStateException("Drawable not added to container!");
		}
		this.isClient = client;
	}

	/**
	 * @deprecated internal
	 */
	@Deprecated
	protected static final ThreadLocal<Boolean> IS_CLIENT = ThreadLocal.withInitial(() -> Boolean.FALSE);
}
