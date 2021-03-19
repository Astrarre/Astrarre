package io.github.astrarre.gui.v0.api;

import java.util.Optional;
import java.util.function.Function;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultScreen;
import io.github.astrarre.gui.internal.vanilla.DefaultScreenHandler;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.networking.internal.ByteBufDataOutput;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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
	static <T> T open(NetworkMember member, Function<RootContainer, T> function) {
		ServerPlayerEntity entity = (ServerPlayerEntity) member;
		Object[] ref = new Object[] {null};
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
				ref[0] = function.apply(container);
				this.contentPanel = container;
				return handler;
			}
		});
		return (T) ref[0];
	}

	/**
	 * opens a new clientside only gui
	 */
	@Environment(EnvType.CLIENT)
	static RootContainer openClientOnly() {
		Screen screen = new DefaultScreen();
		MinecraftClient.getInstance().openScreen(screen);
		return ((ScreenRootAccess)screen).getClientRoot();
	}

	@Environment(EnvType.CLIENT)
	static Optional<RootContainer> currentScreen() {
		return Optional.ofNullable(MinecraftClient.getInstance().currentScreen).map(ScreenRootAccess.class::cast).map(ScreenRootAccess::getClientRoot);
	}

	enum Type {
		/**
		 * @deprecated unsupported ATM
		 */
		@Deprecated
		HUD,
		SCREEN
	}

	Type getType();

	/**
	 * @return true if this RootContainer instance is representing a gui on the client (either clientside, or client-synced)
	 */
	boolean isClient();

	/**
	 * The content panel is the 'root' panel of the screen. Do not transform this
	 * @return the panel where drawables are meant to be attached
	 */
	APanel getContentPanel();

	/**
	 * you <b>MUST</b> call this method for each drawable you add. Some classes will automatically register the component though (calling it anyways doesn't hurt)
	 *
	 * @see APanel adding components will automatically register it
	 */
	void addRoot(Drawable drawable);

	/**
	 * This should only be called once all references that this container may have to the drawable have also been removed.
	 * For example if the same drawable is in the content panel, and inside a list inside that content panel, both have to be removed before this should be called
	 */
	void removeRoot(Drawable drawable);

	/**
	 * The result will be null if on the clientside
	 * if {@link #getType()} == {@link Type#SCREEN}
	 * if {@link #getType()} == {@link Type#HUD} and on the server
	 */
	NetworkMember getViewer();

	@Environment(EnvType.CLIENT)
	<T extends Drawable & Interactable> void setFocus(T drawable);

	/**
	 * @return true if the user is dragging their mouse (in hud/server this is always false)
	 */
	boolean isDragging();

	/**
	 * @return the drawable for the given sync id (or null)
	 * @throws IllegalStateException if called while the container is reading it's contents
	 */
	@Nullable
	Drawable forId(int id);

	int getTick();

	interface OnResize {
		void resize(int width, int height);
	}

	/**
	 * this method only works for client-side guis, if you're serializing components to the client, your component should attach this on the client when it is deserialized
	 * minecraft guis scale in such a way that you don't need to change the size of your component, but you may need to translate it
	 */
	@Environment(EnvType.CLIENT)
	void addResizeListener(OnResize resize);
}
