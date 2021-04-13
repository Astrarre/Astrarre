package io.github.astrarre.gui.v0.api;

import java.util.Iterator;

import com.google.common.collect.Iterators;
import io.github.astrarre.gui.v0.api.access.Container;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ADelegateDrawable extends ADrawable implements Interactable, Container {
	private final int id;
	private ADrawable delegate;

	/**
	 * the delegate is automatically added to any RootContainers this delegate is added to
	 */
	protected ADelegateDrawable(DrawableRegistry.Entry id, ADrawable delegate) {
		super(id);
		this.delegate = delegate;
		this.id = delegate.getSyncId();
	}

	@Environment (EnvType.CLIENT)
	protected ADelegateDrawable(DrawableRegistry.Entry id, NBTagView input) {
		super(id);
		this.id = input.getInt("syncId");
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		this.getDelegate().render0(container, graphics, tickDelta);
	}

	protected ADrawable getDelegate() {
		if (this.delegate == null) {
			this.delegate = this.roots.get(0).forId(this.id);
		}
		return this.delegate;
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		output.putInt("syncId", this.delegate.getSyncId());
	}

	@Override
	public void onRemoved(RootContainer container) {
		super.onRemoved(container);
		this.getDelegate().onRemoved(container);
	}

	@Override
	public Polygon getBounds() {
		return this.getDelegate().getBounds();
	}

	@Override
	public void setBounds(Polygon polygon) {
		super.setBounds(polygon);
		if (this.delegate != null) {
			this.delegate.setBounds(polygon);
		}
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		container.addRoot(this.getDelegate());
	}

	@Override
	public Matrix4f getInvertedMatrix() {
		return this.delegate.getInvertedMatrix();
	}

	@Override
	public Transformation getTransformation() {
		return this.delegate.getTransformation();
	}

	@Override
	public ADrawable setTransformation(Transformation transformation) {
		if(this.delegate != null) {
			this.delegate.setTransformation(transformation);
		}
		return this;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public void mouseMoved(RootContainer container, double mouseX, double mouseY) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable) this.getDelegate()).mouseMoved(container, mouseX, mouseY);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).mouseClicked(container, mouseX, mouseY, button);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseReleased(RootContainer container, double mouseX, double mouseY, int button) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).mouseReleased(container, mouseX, mouseY, button);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseDragged(RootContainer container, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).mouseDragged(container, mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseScrolled(RootContainer container, double mouseX, double mouseY, double amount) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).mouseScrolled(container, mouseX, mouseY, amount);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyPressed(RootContainer container, int keyCode, int scanCode, int modifiers) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).keyPressed(container, keyCode, scanCode, modifiers);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyReleased(RootContainer container, int keyCode, int scanCode, int modifiers) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).keyReleased(container, keyCode, scanCode, modifiers);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean charTyped(RootContainer container, char chr, int modifiers) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).charTyped(container, chr, modifiers);
	}

	@Override
	public boolean handleFocusCycle(RootContainer container, boolean forward) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).handleFocusCycle(container, forward);
	}

	@Override
	public boolean canFocus(RootContainer container) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).canFocus(container);
	}

	@Override
	public void onFocus(RootContainer container) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable) this.getDelegate()).onFocus(container);
	}

	@Override
	public void onLostFocus(RootContainer container) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable) this.getDelegate()).onLostFocus(container);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable) this.getDelegate()).isHovering(container, mouseX, mouseY);
	}

	@Override
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable) this.getDelegate()).mouseHover(container, mouseX, mouseY);
	}

	@Override
	public void onLoseHover(RootContainer container) {
		if (!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable) this.getDelegate()).onLoseHover(container);
	}

	@Override
	public <T extends ADrawable & Interactable> @Nullable T drawableAt(RootContainer container, double x, double y) {
		if (this.delegate instanceof Container) {
			return ((Container) this.delegate).drawableAt(container, x, y);
		} else if (this.delegate instanceof Interactable && ((Interactable) this.delegate).isHovering(container, x, y)) {
			return (T) this.delegate;
		}
		return null;
	}

	@NotNull
	@Override
	public Iterator<ADrawable> iterator() {
		return Iterators.singletonIterator(this.delegate);
	}
}
