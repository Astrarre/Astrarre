package io.github.astrarre.gui.v0.api.panel;

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
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.math.Vector4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Panel extends Drawable implements Interactable, Container {
	public static final int SYNC_CLIENT = 0;
	private static final Logger LOGGER = LogManager.getLogger("Astrarre Panel");
	private static final DrawableRegistry.Entry PANEL = DrawableRegistry.register(Id.create("astrarre-gui-v0", "panel"), Panel::new);
	private final List<Drawable> toDraw;
	protected int index;
	protected Interactable focused, hovered;
	private IntList pendingDrawables;

	public Panel() {
		this(PANEL);
	}

	protected Panel(DrawableRegistry.Entry entry) {
		super(entry);
		this.toDraw = new ArrayList<>();
	}

	@Environment (EnvType.CLIENT)
	private Panel(Input input) {
		this(PANEL, input);
	}

	@Environment (EnvType.CLIENT)
	protected Panel(DrawableRegistry.Entry entry, Input input) {
		super(entry);
		int size = input.readInt();
		this.toDraw = new ArrayList<>(size);
		this.pendingDrawables = new IntArrayList(size);
		for (int i = 0; i < size; i++) {
			this.pendingDrawables.add(input.readInt());
		}
	}

	public static void init() {}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		for (Drawable drawable : this.getToDraw(container)) {
			drawable.render(container, graphics, tickDelta);
		}
	}

	@Override
	public void write0(RootContainer container, Output output) {
		output.writeInt(this.getToDraw(container).size());
		for (Drawable drawable : this.getToDraw(container)) {
			output.writeInt(drawable.getSyncId());
		}
	}

	@Override
	protected void receiveFromServer(RootContainer container, int channel, Input input) {
		super.receiveFromServer(container, channel, input);
		if (channel == SYNC_CLIENT) {
			int id = input.readInt();
			if (this.pendingDrawables == null) {
				this.pendingDrawables = new IntArrayList();
			}
			this.pendingDrawables.add(id);
		}
	}

	private List<Drawable> getToDraw(RootContainer container) {
		if (this.pendingDrawables != null) {
			for (int i = this.pendingDrawables.size() - 1; i >= 0; i--) {
				int val = this.pendingDrawables.getInt(i);
				Drawable drawable = container.forId(val);
				if (drawable != null) {
					this.pendingDrawables.removeInt(i);
					this.toDraw.add(drawable);
				} else {
					LOGGER.warn("Drawable with id " + val + " not found!");
				}
			}

			if (this.pendingDrawables.isEmpty()) {
				this.pendingDrawables = null;
			}
		}
		return this.toDraw;
	}

	/**
	 * If called on the client, the method is ignored. the panel will wait until the server sends the component so it does not desync. This method
	 * also automatically adds the drawable to each of this panel's roots
	 *
	 * @see #addClient(Drawable)
	 */
	public void add(Drawable drawable) {
		if (!this.isClient()) {
			for (RootContainer root : this.roots) {
				root.addRoot(drawable);
			}
			this.getToDraw(this.roots.get(0)).add(0, drawable);
			this.sendToClients(SYNC_CLIENT, output -> output.writeInt(drawable.getSyncId()));
		}
	}

	/**
	 * does not sync to the server, if the method is called on the server it is ignored
	 */
	public void addClient(Drawable drawable) {
		if (this.isClient()) {
			for (RootContainer root : this.roots) {
				root.addRoot(drawable);
			}
			this.getToDraw(this.roots.get(0)).add(drawable);
		}
	}

	@Override
	@Environment (EnvType.CLIENT)
	public void mouseMoved(RootContainer container, double mouseX, double mouseY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables(container)) {
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
	protected Iterable<Interactable> interactables(RootContainer container) {
		return (Iterable) Iterables.filter(this.getToDraw(container), input -> input instanceof Interactable);
	}

	private static void transformation(Interactable interactable, Vector4f vector4f, double x, double y) {
		vector4f.set((float) x, (float) y, 1, 1);
		vector4f.transform(((Drawable) interactable).getInvertedMatrix());
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables(container)) {
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
		for (Interactable interactable : this.interactables(container)) {
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
		for (Interactable interactable : this.interactables(container)) {
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
		for (Interactable interactable : this.interactables(container)) {
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

		List<Drawable> toDraw = this.getToDraw(container);
		for (int i = 0; i < toDraw.size(); i++) {
			int index = (i + this.index) % toDraw.size();
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
		for (Interactable interactable : this.interactables(container)) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.isHovering(container, v3f.getX(), v3f.getY())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		Interactable interactable = this.drawableAt(container, mouseX, mouseY);
		if (this.hovered != interactable && this.hovered != null) {
			this.hovered.onLoseHover(container);
		}
		this.hovered = interactable;
		if (interactable != null) {
			interactable.mouseHover(container, mouseX, mouseY);
		}
	}

	@Override
	public <T extends Drawable & Interactable> T drawableAt(RootContainer container, double x, double y) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables(container)) {
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
			this.index = (index == -1 ? this.getToDraw(container).indexOf(interactable) : index) + 1;
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
		return this.getToDraw(this.roots.get(0)).iterator();
	}
}
