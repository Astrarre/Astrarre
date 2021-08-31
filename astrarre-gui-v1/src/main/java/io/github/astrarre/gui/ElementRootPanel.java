package io.github.astrarre.gui;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.internal.AComponentElement;
import io.github.astrarre.gui.internal.CursorImpl;
import io.github.astrarre.gui.internal.GuiInternal;
import io.github.astrarre.gui.v1.api.AComponent;
import io.github.astrarre.gui.v1.api.FocusableComponent;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.icon.Icons;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.keyboard.KeyboardListener;
import io.github.astrarre.rendering.internal.Renderer3DImpl;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;

public abstract class ElementRootPanel extends ARootPanel implements AComponentElement, Drawable, Selectable {
	final ParentElement element;
	final CursorImpl impl = new CursorImpl(new HashMap<>(), 0, 0, type -> GLFW.glfwGetMouseButton(GuiInternal.Holder.WINDOW_HANDLE, type.glfwId()) == GLFW.GLFW_PRESS);

	public ElementRootPanel(ParentElement element) {
		this.element = element;
	}

	@Override
	public <T extends AComponent & FocusableComponent> void requestFocus(@Nullable T component) {
		super.requestFocus(component);
		this.element.focusOn(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MouseListener & KeyboardListener> T listener() {
		return (T) this;
	}

	@Override
	public Cursor createCursor(double mouseX, double mouseY) {
		CursorImpl copy = new CursorImpl(this.impl);
		copy.x = (float) mouseX;
		copy.y = (float) mouseY;
		return copy;
	}

	@Override
	public Cursor currentCursor() {
		return this.impl;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient client = MinecraftClient.getInstance();
		Renderer3DImpl impl = new Renderer3DImpl(client.textRenderer, matrices, Tessellator.getInstance().getBuffer(), client.getItemRenderer(), this.width(), this.height());
		try {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableDepthTest();
			this.render(this.createCursor(mouseX, mouseY), impl);
			this.impl.x = mouseX;
			this.impl.y = mouseY;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			impl.flush();
		}
	}

	protected abstract int width();

	protected abstract int height();

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		// todo api for this
	}

	public static class ScreenImpl extends ElementRootPanel {
		public ScreenImpl(Screen element) {
			super(element);
		}

		@Override
		protected int width() {
			return ((Screen)this.element).width;
		}

		@Override
		protected int height() {
			return ((Screen)this.element).height;
		}
	}
}
