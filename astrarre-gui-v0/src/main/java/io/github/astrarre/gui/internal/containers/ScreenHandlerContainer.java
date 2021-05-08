package io.github.astrarre.gui.internal.containers;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;
import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ResizeListenerAccess;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.access.SlotAddAccess;
import io.github.astrarre.gui.internal.mixin.ScreenHandlerAccess;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.fabric.MinecraftServers;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ScreenHandlerContainer extends RootContainerInternal implements SlotAddAccess {
	@Environment (EnvType.CLIENT) public HandledScreen<?> screen;
	@Environment (EnvType.CLIENT) public List<OnResize> resizeList;
	public ScreenHandler handler;
	private boolean isClient;

	public ScreenHandlerContainer(ScreenHandler handler) {
		this.handler = handler;
	}

	public ScreenHandlerContainer(ScreenHandler handler, NBTagView input) {
		super(e -> {
			((ScreenHandlerContainer) e).handler = handler;
			((ScreenHandlerContainer) e).isClient = true;
		}, input);
	}

	@Override
	public Type getType() {
		return Type.SCREEN;
	}

	@Override
	public boolean isClient() {
		return this.isClient;
	}

	@Override
	public NetworkMember getViewer() {
		if (this.isClient) {
			return null;
		}
		return (NetworkMember) Iterables.tryFind(MinecraftServers.activeServer.getPlayerManager().getPlayerList(), p -> p.currentScreenHandler == this.handler).orNull();
	}

	@Override
	public boolean isDragging() {
		return this.isClient && this.screen != null && this.screen.isDragging();
	}

	@Override
	@Environment (EnvType.CLIENT)
	public void addResizeListener(OnResize resize) {
		if (this.screen == null) {
			if (this.resizeList == null) {
				this.resizeList = new ArrayList<>();
			}
			this.resizeList.add(resize);
		} else {
			((ResizeListenerAccess) this.screen).addResizeListener(resize);
		}
	}

	@Override
	public int getWidth() {
		if(this.isClient) {
			return this.screen.width;
		}
		return -1;
	}

	@Override
	public int getHeight() {
		if(this.isClient) {
			return this.screen.height;
		}
		return -1;
	}

	@Override
	public void addSlot(Slot slot) {
		((ScreenHandlerAccess) this.handler).callAddSlot(slot);
	}

	@Override
	public <T extends ADrawable & Interactable> void setFocus(T drawable) {
		super.setFocus(drawable);
		((ScreenRootAccess) this.screen).astrarre_focusPanel();
	}
}
