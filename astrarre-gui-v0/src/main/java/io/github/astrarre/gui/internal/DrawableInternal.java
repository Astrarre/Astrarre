package io.github.astrarre.gui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import org.jetbrains.annotations.ApiStatus;

public abstract class DrawableInternal {

	public static final int UNINITIALIZED_ID = -1;
	// internal members
	int id = -1;

	List<RootContainer> rootsInternal = new ArrayList<>();
	public List<RootContainer> roots = Collections.unmodifiableList(this.rootsInternal);
	boolean isClient;

	protected DrawableInternal() {
		this.isClient = IS_CLIENT.get();
	}

	public int getSyncId() {
		return this.id;
	}

	@ApiStatus.OverrideOnly
	protected void receiveFromServer(RootContainer container, int channel, NBTagView input) {
	}

	@ApiStatus.OverrideOnly
	protected void receiveFromClient(RootContainer container, NetworkMember member, int channel, NBTagView input) {
	}

	@ApiStatus.OverrideOnly
	protected void onAdded(RootContainer container) {
	}

	public boolean isClient() {
		return this.isClient;
	}

	protected abstract void write0(RootContainer container, NBTagView.Builder output);

	/**
	 * @deprecated internal
	 */
	@Deprecated
	protected static final ThreadLocal<Boolean> IS_CLIENT = ThreadLocal.withInitial(() -> Boolean.FALSE);
}
