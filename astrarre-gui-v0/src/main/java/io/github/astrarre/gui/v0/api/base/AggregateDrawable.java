package io.github.astrarre.gui.v0.api.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Container;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.math.Vector4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class AggregateDrawable extends Drawable implements Interactable, Container {
	private static final Logger LOGGER = LogManager.getLogger("ListWidget");
	private static final int ADD_DRAWABLE = 0, REMOVE_DRAWABLE = 1;
	protected final List<Drawable> drawables;
	protected IntList pendingDrawables;
	protected int focusIndex;
	protected Interactable focused, hovered;

	protected AggregateDrawable(DrawableRegistry.Entry id) {
		super(id);
		this.drawables = new ArrayList<>();
	}

	@Environment (EnvType.CLIENT)
	protected AggregateDrawable(DrawableRegistry.Entry id, Input input) {
		super(id);
		int drawables = input.readInt();
		this.drawables = new ArrayList<>(drawables);
		IntList list = this.pendingDrawables = new IntArrayList(drawables);
		for (int i = 0; i < drawables; i++) {
			list.add(input.readInt());
		}
	}

	/**
	 * If called on the client, the method is ignored. This method also automatically adds the drawable to each of this panel's roots
	 *
	 * @see #addClient(Drawable)
	 */
	public void add(Drawable drawable) {
		if (!this.isClient()) {
			for (RootContainer root : this.roots) {
				root.addRoot(drawable);
			}
			if (this.onAdd(drawable)) {
				this.sendToClients(ADD_DRAWABLE, output -> output.writeInt(drawable.getSyncId()));
			}
		}
	}

	/**
	 * If called on the client, the method is ignored. This method does <b>NOT</b> automatically remove the drawable from the panel's roots because the same drawable may be used in multiple places
	 * @see #removeClient(Drawable)
	 */
	public void remove(Drawable drawable) {
		if(!this.isClient() && this.onRemove(drawable)) {
			this.sendToClients(REMOVE_DRAWABLE, output -> output.writeInt(drawable.getSyncId()));
		}
	}

	protected void onDrawablesChange() {}

	protected boolean onRemove(Drawable drawable) {
		this.drawables.remove(drawable);
		this.onDrawablesChange();
		return true;
	}

	protected boolean onAdd(Drawable drawable) {
		this.drawables.add(0, drawable);
		this.onDrawablesChange();
		return true;
	}

	protected boolean onSync(Drawable drawable) {
		return true;
	}

	protected boolean onSyncRemove(Drawable drawable) {
		return true;
	}

	/**
	 * does not sync to the server, if the method is called on the server it is ignored
	 */
	public void addClient(Drawable drawable) {
		if (this.isClient()) {
			for (RootContainer root : this.roots) {
				root.addRoot(drawable);
			}
			if (this.onAdd(drawable)) {
				this.drawables.add(0, drawable);
				this.onDrawablesChange();
			}
		}
	}

	public void removeClient(Drawable drawable) {
		if(this.isClient() && this.onRemove(drawable)) {
			this.drawables.remove(drawable);
			this.onDrawablesChange();
		}
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		output.writeInt(this.drawables.size());
		for (Drawable drawable : this.drawables) {
			output.writeInt(drawable.getSyncId());
		}
	}

	@Override
	protected void receiveFromServer(RootContainer container, int channel, Input input) {
		super.receiveFromServer(container, channel, input);
		if (channel == ADD_DRAWABLE) {
			int id = input.readInt();
			Drawable drawable = container.forId(id);
			if (drawable != null && this.onSync(drawable)) {
				this.drawables.add(0, drawable);
				this.onDrawablesChange();
			} else {
				LOGGER.warn("No component found for id " + id);
			}
		} else if(channel == REMOVE_DRAWABLE) {
			int id = input.readInt();
			Drawable drawable = container.forId(id);
			if (drawable != null && this.onSyncRemove(drawable)) {
				this.drawables.remove(drawable);
				this.onDrawablesChange();
			} else {
				LOGGER.warn("No component found for id " + id);
			}
		}
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		if (this.pendingDrawables != null) {
			for (int i = this.pendingDrawables.size() - 1; i >= 0; i--) {
				Drawable drawable = container.forId(this.pendingDrawables.getInt(i));
				if (drawable != null && this.onSync(drawable)) {
					this.pendingDrawables.removeInt(i);
					this.drawables.add(drawable);
					this.onDrawablesChange();
				}
			}
		} else {
			this.drawables.forEach(container::addRoot);
		}
	}

	@Override
	@Environment (EnvType.CLIENT)
	public void mouseMoved(RootContainer container, double mouseX, double mouseY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY())) {
				interactable.mouseMoved(container, v3f.getX(), v3f.getY());
				return;
			}
		}
	}

	@SuppressWarnings ({
			"unchecked",
			"rawtypes"
	})
	protected Iterable<Interactable> interactables() {
		return (Iterable) Iterables.filter(this.drawables, input -> input instanceof Interactable);
	}

	private static void transformation(Interactable interactable, Vector4f vector4f, double x, double y) {
		vector4f.set((float) x, (float) y, 1, 1);
		vector4f.transform(((Drawable) interactable).getInvertedMatrix());
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.mouseClicked(container, v3f.getX(), v3f.getY(), button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseReleased(RootContainer container, double mouseX, double mouseY, int button) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.mouseReleased(container, v3f.getX(), v3f.getY(), button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseDragged(RootContainer container, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			transformation(interactable, v3f, mouseX, mouseY);
			float mX = v3f.getX(), mY = v3f.getY();
			transformation(interactable, v3f, deltaX, deltaY);
			if (((Drawable) interactable).getBounds().isInside(mX, mY) && interactable
					                                                              .mouseDragged(container, mX, mY, button, v3f.getX(), v3f.getY())) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseScrolled(RootContainer container, double mouseX, double mouseY, double amount) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.mouseScrolled(container, v3f.getX(), v3f.getY(), amount)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyPressed(RootContainer container, int keyCode, int scanCode, int modifiers) {
		if (this.focused != null) {
			return this.focused.keyPressed(container, keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyReleased(RootContainer container, int keyCode, int scanCode, int modifiers) {
		if (this.focused != null) {
			return this.focused.keyReleased(container, keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean charTyped(RootContainer container, char chr, int modifiers) {
		if (this.focused != null) {
			return this.focused.charTyped(container, chr, modifiers);
		}
		return false;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean handleFocusCycle(RootContainer container, boolean forward) {
		if (this.focused != null && this.focused.handleFocusCycle(container, forward)) {
			return true;
		}

		List<Drawable> toDraw = this.drawables;
		for (int i = 0; i < toDraw.size(); i++) {
			int index = (i + this.focusIndex) % toDraw.size();
			if (!forward) {
				index = (toDraw.size() - 1) - index;
			}

			Drawable drawable = toDraw.get(i);
			if (drawable instanceof Interactable) {
				Interactable interactable = (Interactable) drawable;
				if (interactable.canFocus(container) || interactable.handleFocusCycle(container, forward)) {
					this.setFocused(container, interactable, index);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.isHovering(container, v3f.getX(), v3f.getY())) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		Interactable interactable = this.drawableAt(container, mouseX, mouseY);
		if (this.hovered != interactable && this.hovered != null) {
			this.hovered.onLoseHover(container);
		}
		this.hovered = interactable;
		if (interactable != null) {
			Vector4f v3f = new Vector4f(0, 0, 0, 1);
			transformation(interactable, v3f, mouseX, mouseY);
			interactable.mouseHover(container, v3f.getX(), v3f.getY());
		}
	}

	@Override
	public <T extends Drawable & Interactable> T drawableAt(RootContainer container, double x, double y) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			transformation(interactable, v3f, x, y);
			if (((Drawable) interactable).getBounds().isInside(v3f.getX(), v3f.getY())) {
				if (interactable instanceof Container) {
					return ((Container) interactable).drawableAt(container, v3f.getX(), v3f.getY());
				} else if (interactable.isHovering(container, v3f.getX(), v3f.getY())) {
 					return (T) interactable;
				}
			}
		}

		return null;
	}

	/**
	 * @see RootContainer#setFocus(Drawable)
	 * @deprecated internal
	 */
	@Deprecated
	public void setFocused(RootContainer container, @Nullable Interactable interactable, int index) {
		Interactable old = this.focused;
		this.focused = interactable;
		if (interactable != null) {
			interactable.onFocus(container);
			this.focusIndex = (index == -1 ? this.drawables.indexOf(interactable) : index) + 1;
		}

		if (old != null) {
			old.onLostFocus(container);
		}
	}

	@Nullable
	public Interactable getFocused() {
		return this.focused;
	}

	@NotNull
	@Override
	public Iterator<Drawable> iterator() {
		return this.drawables.iterator();
	}
}
