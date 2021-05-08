package io.github.astrarre.gui.v0.api;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultScreen;
import io.github.astrarre.gui.internal.vanilla.DefaultScreenHandler;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.container.ContainerGUI;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * root container, this is not meant to be implemented. Astrarre implements it for Screen and HUD
 */
public interface RootContainer {
	static void openContainer(NetworkMember member, BiFunction<RootContainer, NetworkMember, ContainerGUI> function) {
		openC(member, container -> {
			function.apply(container, member).initContainer();
		});
	}

	static void openC(NetworkMember member, Consumer<RootContainer> consumer) {
		open(member, container -> {
			consumer.accept(container);
			return null;
		});
	}

	/**
	 * @see #openC(NetworkMember, Consumer)
	 * @return the value returned from the function
	 */
	static <T> T open(NetworkMember member, Function<RootContainer, T> function) {
		ServerPlayerEntity entity = (ServerPlayerEntity) member;
		Object[] ref = new Object[] {null};
		entity.openHandledScreen(new NamedScreenHandlerFactory() {
			@Override
			public Text getDisplayName() {
				return new LiteralText("Astrarre GUI");
			}

			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
				DefaultScreenHandler handler = new DefaultScreenHandler(syncId);
				RootContainerInternal container = ((ScreenRootAccess)handler).getRoot();
				ref[0] = function.apply(container);
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

	Serializer<Type> TYPE_SERIALIZER = Serializer.ofEnum(Type.class);
	enum Type {
		/**
		 * @deprecated unsupported ATM
		 */
		@Deprecated
		HUD,
		SCREEN,
		/**
		 * an REI recipe screen
		 */
		REI_RECIPE
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
	void addRoot(ADrawable drawable);

	/**
	 * This should only be called once all references that this container may have to the drawable have also been removed.
	 * For example if the same drawable is in the content panel, and inside a list inside that content panel, both have to be removed before this should be called
	 */
	void removeRoot(ADrawable drawable);

	/**
	 * The result will be null if on the clientside or if {@link #getType()} == {@link Type#HUD} and on the server
	 */
	@Nullable
	NetworkMember getViewer();

	/**
	 * may not work as intended if {@link #getType()} == {@link Type#REI_RECIPE}
	 */
	@Environment(EnvType.CLIENT)
	<T extends ADrawable & Interactable> void setFocus(T drawable);

	/**
	 * @return true if the user is dragging their mouse (in hud/server this is always false)
	 */
	boolean isDragging();

	/**
	 * @return the drawable for the given sync id (or null)
	 * @throws IllegalStateException if called while the container is reading it's contents
	 */
	@Nullable ADrawable forId(int id);

	/**
	 * @return the current number of ticks the screen has been open, if not implemented, returns -1
	 */
	int getTick();

	void addCloseListener(Runnable onClose);

	interface OnResize {
		void resize(int width, int height);
	}

	/**
	 * this method only works for client-side guis, if you're serializing components to the client, your component should attach this on the client when it is deserialized
	 * minecraft guis scale in such a way that you don't need to change the size of your component, but you may need to translate it
	 */
	@Environment(EnvType.CLIENT)
	void addResizeListener(OnResize resize);

	Serializer<ADrawable> getSerializer();

	/**
	 * @return for screens and HUD, returns the window width. For screen handlers, returns -1. And for REI recipes, returns the bounds of the recipe display
	 */
	int getWidth();

	/**
	 * @return for screens and HUD, returns the window height. For screen handlers, returns -1. And for REI recipes, returns the bounds of the recipe display
	 */
	int getHeight();
}
