package io.github.astrarre.gui.internal.containers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ResizeListenerAccess;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.access.SlotAddAccess;
import io.github.astrarre.gui.internal.mixin.access.ScreenHandlerAccess;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.fabric.MinecraftServers;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ScreenHandlerContainer extends RootContainerInternal implements SlotAddAccess {
	@Environment (EnvType.CLIENT) public HandledScreen<?> screen;
	@Environment (EnvType.CLIENT) public List<OnResize> resizeList;
	private ScreenHandler handler;
	private boolean isClient;
	public ScreenHandlerContainer(ScreenHandler handler) {
		this.handler = handler;
	}
	public ScreenHandlerContainer(ScreenHandler handler, Input input) {
		super(e -> ((ScreenHandlerContainer) e).handler = handler, input);
		this.isClient = true;
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
	public Iterable<NetworkMember> getViewers() {
		if (this.isClient) {
			return Collections.emptyList();
		}
		return Iterables.transform(
				Iterables.filter(MinecraftServers.activeServer.getPlayerManager().getPlayerList(), p -> p.currentScreenHandler == this.handler),
				NetworkMember.class::cast);
	}
	@Override
	public boolean isDragging() {
		return this.isClient && this.screen != null && this.screen.isDragging();
	}
	@Override
	@Environment (EnvType.CLIENT)
	public void addResizeListener(OnResize resize) {
		if (this.screen == null) {
			if(this.resizeList == null) {
				this.resizeList = new ArrayList<>();
			}
			this.resizeList.add(resize);
		} else {
			((ResizeListenerAccess) this.screen).addResizeListener(resize);
		}
	}

	@Override
	public void addSlot(Slot slot) {
		((ScreenHandlerAccess) this.handler).callAddSlot(slot);
	}

	@Override
	public <T extends Drawable & Interactable> void setFocus(T drawable) {
		super.setFocus(drawable);
		((ScreenRootAccess) this.screen).astrarre_focusPanel();
	}
}