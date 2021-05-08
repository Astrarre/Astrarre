package io.github.astrarre.gui.internal.access;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;

import net.minecraft.network.PacketByteBuf;

public interface ScreenRootAccess {
	RootContainerInternal getRoot();

	default RootContainerInternal getClientRoot() {
		throw new UnsupportedOperationException();
	}

	void readRoot(NBTagView buf);

	void astrarre_focusPanel();
}
