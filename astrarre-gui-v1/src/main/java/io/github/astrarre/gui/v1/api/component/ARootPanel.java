package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.gui.internal.GuiInternal;
import io.github.astrarre.gui.internal.access.PanelScreenAccess;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.Edge;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A root gui object
 */
public class ARootPanel extends APanel {
	public final Access<Runnable> onClose = new Access<>(Id.create("astrarre", "root_panel_close"), array -> () -> {
		for(Runnable runnable : array) {
			runnable.run();
		}
	});

	public ARootPanel() {
	}

	/**
	 * @param enable if true, renders a dark background behind the screen, similar to how all other vanilla container guis work
	 */
	public ARootPanel darkBackground(boolean enable) {
		this.set(BACKGROUND, enable);
		return this;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		if(this.is(BACKGROUND)) {
			render.fill().rect(0xc0101010, 0, 0, this.getWidth(), this.getHeight());
		}
		super.render0(cursor, render);
	}

	/**
	 * Changes focused to the given component, this gives it priority for keyboard events.
	 * @param component can be a component that is not in the current group
	 */
	public <T extends AComponent & FocusableComponent> void requestFocus(@Nullable T component) {
		AComponent prev = this.focused;
		if(prev != null) {
			((FocusableComponent) prev).setFocused(true);
		}
		this.focused = component;
		if(component != null) {
			component.setFocused(false);
		}
	}


	/**
	 * Opens a new client-only gui
	 * @see ServerPanel#openHandled(PlayerEntity, ServerPanel.ClientInit, ServerPanel.ServerInit)
	 */
	@Environment(EnvType.CLIENT)
	public static ARootPanel open() {
		var screen = new Screen(GuiInternal.TEXT) {};
		MinecraftClient.getInstance().setScreen(screen);
		return getPanel(screen);
	}

	@Edge
	@Environment(EnvType.CLIENT)
	public static ARootPanel getPanel(Screen screen) {
		return ((PanelScreenAccess) screen).getRootPanel();
	}

	@Override
	protected boolean cursor(Cursor cursor, CursorCallback consumer) {
		if(this.find(this.focused, t -> { // maybe instead just temporary disable the listeners or smth
			this.focused.set(AComponent.SKIP_MOUSEVENT, true);
			if(t.component() instanceof MouseListener l) {
				Cursor transformed = cursor.transformed(t.transform().invert());
				return t.component().isIn(transformed) && consumer.accept(transformed, t, l);
			}
			return false;
		})) {
			return true;
		}

		boolean value = super.cursor(cursor, consumer);
		if(this.focused != null) {
			this.focused.set(AComponent.SKIP_MOUSEVENT, false);
		}
		return value;
	}
}
