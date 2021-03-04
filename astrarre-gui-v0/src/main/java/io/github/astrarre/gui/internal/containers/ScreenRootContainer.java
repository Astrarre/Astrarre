package io.github.astrarre.gui.internal.containers;

import java.util.Collections;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.network.NetworkMember;

import net.minecraft.client.gui.screen.Screen;

public class ScreenRootContainer<T extends Screen> extends RootContainerInternal {
	public T screen;

	public ScreenRootContainer(T screen, Input input) {
		super(c -> ((ScreenRootContainer)c).screen = screen,input);
	}

	@Override
	public Type getType() {
		return Type.SCREEN;
	}

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public Iterable<NetworkMember> getViewers() {
		return Collections.emptyList();
	}

	@Override
	public boolean isDragging() {
		return this.screen.isDragging();
	}

	@Override
	public <T extends Drawable & Interactable> void setFocus(T drawable) {
		super.setFocus(drawable);
		((ScreenRootAccess)this.screen).astrarre_focusPanel();
	}
}
