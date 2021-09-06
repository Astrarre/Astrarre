package io.github.astrarre.gui.internal;

import java.util.Arrays;
import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.component.APanel;
import io.github.astrarre.gui.v1.api.component.FocusableComponent;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.listener.cursor.ClickType;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.listener.keyboard.KeyboardListener;
import io.github.astrarre.gui.v1.api.util.ComponentTransform;
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
	public ParentElement element;
	final CursorImpl impl = new CursorImpl(0, 0);

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
		Cursor curr = this.currentCursor(), at = this.createCursor(mouseX, mouseY);
		float deltaX = (float) mouseX - curr.x(), deltaY = (float) mouseY - curr.y();
		if(deltaX != 0 || deltaY != 0) {
			if(curr.isPressed(ClickType.Standard.LEFT)) { // we use this instead of Element's dragged because that one requires focus
				this.listener().mouseDragged(at, ClickType.Standard.LEFT, deltaX, deltaY);
			}
			this.listener().mouseMoved(at, (float) mouseX - curr.x(), (float) mouseY - curr.y());
		}

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

	public void setBoundsInternal(float width, float height) {
		this.lockBounds(false);
		this.setBounds(width, height);
		this.lockBounds(true);
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

	@Override
	protected void recomputeBounds() {
	}

	@Override
	public APanel add(ComponentTransform<?>... component) {
		this.cmps.addAll(Arrays.asList(component));
		return this;
	}

	@Override
	public APanel remove(ComponentTransform<?>... component) {
		this.cmps.addAll(Arrays.asList(component));
		return this;
	}
}
