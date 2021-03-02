package io.github.astrarre.gui.internal.containers;

import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.networking.v0.api.network.NetworkMember;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

public class ScreenRootContainer<T extends Screen> extends RootContainerInternal {
	public final T screen;

	public ScreenRootContainer(T screen) {
		this.screen = screen;
		List<Element> children = (List<Element>) this.screen.children();
		children.add(new PanelElement(this.getContentPanel()));
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
	public <C extends Drawable & Interactable> void setFocus(C drawable) {
		this.panel.setFocused(drawable, -1);
	}

	@Override
	public boolean isDragging() {
		return this.screen.isDragging();
	}

	public static class PanelElement implements Element {
		protected final Panel panel;

		public PanelElement(Panel interactable) {this.panel = interactable;}

		@Override
		public void mouseMoved(double mouseX, double mouseY) {
			this.panel.mouseMoved(mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return this.panel.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return this.panel.mouseReleased(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			return this.panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
			return this.panel.mouseScrolled(mouseX, mouseY, amount);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			return this.panel.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
			return this.panel.keyReleased(keyCode, scanCode, modifiers);
		}

		@Override
		public boolean charTyped(char chr, int modifiers) {
			return this.panel.charTyped(chr, modifiers);
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return this.panel.mouseHover(mouseX, mouseY);
		}

		@Override
		public boolean changeFocus(boolean lookForwards) {
			return this.panel.handleFocusCycle(lookForwards);
		}
	}
}
