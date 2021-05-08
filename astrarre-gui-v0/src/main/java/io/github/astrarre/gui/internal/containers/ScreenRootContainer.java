package io.github.astrarre.gui.internal.containers;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ResizeListenerAccess;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.network.NetworkMember;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;

public class ScreenRootContainer<T extends Screen> extends RootContainerInternal {
	public T screen;

	public ScreenRootContainer(T screen) {
		this.screen = screen;
	}

	public ScreenRootContainer(T screen, NBTagView input) {
		super(c -> ((ScreenRootContainer) c).screen = screen, input);
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
	public NetworkMember getViewer() {
		return null;
	}

	@Override
	public boolean isDragging() {
		return this.screen.isDragging();
	}

	@Override
	public void addResizeListener(OnResize resize) {
		((ResizeListenerAccess) this.screen).addResizeListener(resize);
	}

	@Override
	public int getWidth() {
		return this.screen.width;
	}

	@Override
	public int getHeight() {
		return this.screen.height;
	}

	@Override
	public <T extends ADrawable & Interactable> void setFocus(T drawable) {
		super.setFocus(drawable);
		((ScreenRootAccess) this.screen).astrarre_focusPanel();
	}
}
