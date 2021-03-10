package io.github.astrarre.gui.v0.api.base;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.util.Polygon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class DelegateDrawable extends Drawable implements Interactable {
	private final int id;
	private Drawable delegate;

	/**
	 * the delegate is automatically added to any RootContainers this delegate is added to
	 */
	protected DelegateDrawable(DrawableRegistry.Entry id, Drawable delegate) {
		super(id);
		this.delegate = delegate;
		this.id = delegate.getSyncId();
	}

	@Environment(EnvType.CLIENT)
	protected DelegateDrawable(DrawableRegistry.Entry id, Input input) {
		super(id);
		this.id = input.readInt();
	}
	
	protected Drawable getDelegate() {
		if(this.delegate == null) {
			this.delegate = this.roots.get(0).forId(this.id);
		}
		return this.delegate;
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		this.getDelegate().render(container, graphics, tickDelta);
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		container.addRoot(this.getDelegate());
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		output.writeInt(this.delegate.getSyncId());
	}

	@Override
	@Environment (EnvType.CLIENT)
	public void mouseMoved(RootContainer container, double mouseX, double mouseY) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).mouseMoved(container, mouseX, mouseY);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).mouseClicked(container, mouseX, mouseY, button);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseReleased(RootContainer container, double mouseX, double mouseY, int button) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).mouseReleased(container, mouseX, mouseY, button);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseDragged(RootContainer container, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).mouseDragged(container, mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseScrolled(RootContainer container, double mouseX, double mouseY, double amount) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).mouseScrolled(container, mouseX, mouseY, amount);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyPressed(RootContainer container, int keyCode, int scanCode, int modifiers) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).keyPressed(container, keyCode, scanCode, modifiers);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyReleased(RootContainer container, int keyCode, int scanCode, int modifiers) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).keyReleased(container, keyCode, scanCode, modifiers);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean charTyped(RootContainer container, char chr, int modifiers) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).charTyped(container, chr, modifiers);
	}

	@Override
	public boolean handleFocusCycle(RootContainer container, boolean forward) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).handleFocusCycle(container, forward);
	}

	@Override
	public boolean canFocus(RootContainer container) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).canFocus(container);
	}

	@Override
	public void onFocus(RootContainer container) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).onFocus(container);
	}

	@Override
	public void onLostFocus(RootContainer container) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).onLostFocus(container);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).isHovering(container, mouseX, mouseY);
	}

	@Override
	public void onLoseHover(RootContainer container) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).onLoseHover(container);
	}

	@Override
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).mouseHover(container, mouseX, mouseY);
	}

	@Override
	public Polygon getBounds() {
		return this.getDelegate().getBounds();
	}

	@Override
	public void setBounds(Polygon polygon) {
		super.setBounds(polygon);
		this.delegate.setBounds(polygon);
	}
}
