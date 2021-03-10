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
	
	protected DelegateDrawable(RootContainer rootContainer, DrawableRegistry.Entry id, Drawable delegate) {
		super(rootContainer, id);
		this.delegate = delegate;
		this.id = delegate.getSyncId();
	}
	
	protected DelegateDrawable(RootContainer container, DrawableRegistry.Entry id, Input input) {
		super(container, id);
		this.id = input.readInt();
	}
	
	protected Drawable getDelegate() {
		if(this.delegate == null) {
			this.delegate = this.rootContainer.forId(this.id);
		}
		return this.delegate;
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		this.getDelegate().render(graphics, tickDelta);
	}

	@Override
	protected void write0(Output output) {
		output.writeInt(this.delegate.getSyncId());
	}


	@Override
	@Environment (EnvType.CLIENT)
	public void mouseMoved(double mouseX, double mouseY) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).mouseMoved(mouseX, mouseY);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).mouseClicked(mouseX, mouseY, button);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).mouseReleased(mouseX, mouseY, button);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean charTyped(char chr, int modifiers) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).charTyped(chr, modifiers);
	}

	@Override
	public boolean handleFocusCycle(boolean forward) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).handleFocusCycle(forward);
	}

	@Override
	public boolean canFocus() {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).canFocus();
	}

	@Override
	public void onFocus() {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).onFocus();
	}

	@Override
	public void onLostFocus() {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).onLostFocus();
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean isHovering(double mouseX, double mouseY) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return false;
		}
		return ((Interactable)this.getDelegate()).isHovering(mouseX, mouseY);
	}

	@Override
	public void onLoseHover() {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).onLoseHover();
	}

	@Override
	public void mouseHover(double mouseX, double mouseY) {
		if(!(this.getDelegate() instanceof Interactable)) {
			return;
		}
		((Interactable)this.getDelegate()).mouseHover(mouseX, mouseY);
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
