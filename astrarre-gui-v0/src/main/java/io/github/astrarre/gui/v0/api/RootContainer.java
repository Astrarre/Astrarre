package io.github.astrarre.gui.v0.api;

import java.util.function.Consumer;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultScreenHandler;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.networking.internal.ByteBufDataOutput;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.stripper.Hide;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;

/**
 * root container, this is not meant to be implemented. Astrarre implements it for Screen and HUD
 */
public interface RootContainer {
	static void open(NetworkMember member, Consumer<RootContainer> consumer) {
		ServerPlayerEntity entity = (ServerPlayerEntity) member;
		entity.openHandledScreen(new ExtendedScreenHandlerFactory() {
			private RootContainerInternal contentPanel;

			@Override
			public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
				this.contentPanel.write(new ByteBufDataOutput(buf));
			}

			@Override
			public Text getDisplayName() {
				return new LiteralText("astrarre filler text");
			}

			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
				DefaultScreenHandler handler = new DefaultScreenHandler(syncId);
				RootContainerInternal container = ((ScreenRootAccess)handler).getRoot();
				consumer.accept(container);
				this.contentPanel = container;
				return handler;
			}
		});
	}

	enum Type {
		HUD,
		SCREEN
	}

	Type getType();

	boolean isClient();

	Panel getContentPanel();

	/**
	 * if {@link #getType()} == {@link Type#SCREEN} The iterable will be empty if on the clientside
	 * if {@link #getType()} == {@link Type#HUD} and on the server, the iterable will just have the player with the hud
	 */
	@Hide
	Iterable<NetworkMember> getViewers();

	@Environment(EnvType.CLIENT)
	<T extends Drawable & Interactable> void setFocus(T drawable);

	/**
	 * @return true if the user is dragging their mouse (in hud/server this is always false)
	 */
	boolean isDragging();

	/**
	 * null if the container has not yet read the drawable or it does not exist
	 * @return the drawable for the given sync id
	 */
	@Nullable
	Drawable forId(int id);

	int tick();
}
