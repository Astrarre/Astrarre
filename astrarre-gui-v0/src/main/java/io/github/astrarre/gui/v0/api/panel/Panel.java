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
	private static final Logger LOGGER = LogManager.getLogger("Astrarre Panel");
	private static final DrawableRegistry.Entry PANEL = DrawableRegistry.register(Id.create("astrarre-gui-v0", "panel"), Panel::new);
	public static final int SYNC_CLIENT = 0;
	private final List<Drawable> toDraw;
	protected int index;
	protected Interactable focused;
	private IntList pendingDrawables;

	public Panel(RootContainer rootContainer) {
		this(rootContainer, PANEL);
	}

	protected Panel(RootContainer rootContainer, DrawableRegistry.Entry entry) {
		super(rootContainer, entry);
		this.toDraw = new ArrayList<>();
	}

	@Environment(EnvType.CLIENT)
	public Panel(RootContainer rootContainer, Input input) {
		this(rootContainer, PANEL, input);
	}

	@Environment(EnvType.CLIENT)
	protected Panel(RootContainer rootContainer, DrawableRegistry.Entry entry, Input input) {
		super(rootContainer, entry);
		int size = input.readInt();
		this.toDraw = new ArrayList<>(size);
		this.pendingDrawables = new IntArrayList(size);
		for (int i = 0; i < size; i++) {
			this.pendingDrawables.add(input.readInt());
		}
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		for (Drawable drawable : this.getToDraw()) {
			drawable.render(graphics, tickDelta);
		}
	}

	@Override
	public void write0(Output output) {
		output.writeInt(this.getToDraw().size());
		for (Drawable drawable : this.getToDraw()) {
			output.writeInt(drawable.getSyncId());
		}
	}

	private List<Drawable> getToDraw() {
		if (this.pendingDrawables != null) {
			for (int i = this.pendingDrawables.size() - 1; i >= 0; i--) {
				int val = this.pendingDrawables.getInt(i);
				Drawable drawable = this.rootContainer.forId(val);
				if (drawable != null) {
					this.pendingDrawables.removeInt(i);
					this.toDraw.add(drawable);
				} else {
					LOGGER.warn("Drawable with id " + val + " not found!");
				}
			}

			if(this.pendingDrawables.isEmpty()) {
				this.pendingDrawables = null;
			}
		}
		return this.toDraw;
	}

	@Override
	protected void receiveFromServer(int channel, Input input) {
		super.receiveFromServer(channel, input);
		if (channel == SYNC_CLIENT) {
			Drawable drawable = read(this.rootContainer, input);
			this.getToDraw().add(drawable);
		}
	}

	/**
	 * If called on the client, the method is ignored. the panel will wait until the server sends the component so it does not desync
	 *
	 * @see #addClient(Drawable)
	 */
	public void add(Drawable drawable) {
		if (!this.rootContainer.isClient()) {
			this.getToDraw().add(0, drawable);
			this.sendToClients(SYNC_CLIENT, drawable::write);
		}
	}

	/**
	 * does not sync to the server, if the method is called on the server it is ignored
	 */
	public void addClient(Drawable drawable) {
		if (this.rootContainer.isClient()) {
			this.getToDraw().add(drawable);
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void mouseMoved(double mouseX, double mouseY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY())) {
				interactable.mouseMoved(v3f.getX(), v3f.getY());
				return;
			}
		}
	}

	@SuppressWarnings ({
			"unchecked",
			"rawtypes"
	})
	protected Iterable<Interactable> interactables() {
		return (Iterable) Iterables.filter(this.getToDraw(), input -> input instanceof Interactable);
	}

	private static void transformation(Interactable interactable, Vector4f vector4f, double x, double y) {
		vector4f.set((float) x, (float) y, 1, 1);
		vector4f.transform(((Drawable) interactable).getInvertedMatrix());
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.mouseClicked(v3f.getX(), v3f.getY(), button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.mouseReleased(v3f.getX(), v3f.getY(), button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			transformation(interactable, v3f, mouseX, mouseY);
			float mX = v3f.getX(), mY = v3f.getY();
			transformation(interactable, v3f, deltaX, deltaY);
			if (((Drawable)interactable).getBounds().isInside(mX, mY) && interactable.mouseDragged(mX, mY, button, v3f.getX(), v3f.getY())) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.mouseScrolled(v3f.getX(), v3f.getY(), amount)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.focused != null) {
			return this.focused.keyPressed(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (this.focused != null) {
			return this.focused.keyReleased(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean charTyped(char chr, int modifiers) {
		if (this.focused != null) {
			return this.focused.charTyped(chr, modifiers);
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handleFocusCycle(boolean forward) {
		if (this.focused != null && this.focused.handleFocusCycle(forward)) {
			return true;
		}

		for (int i = 0; i < this.getToDraw().size(); i++) {
			int index = (i + this.index) % this.getToDraw().size();
			if (!forward) {
				index = (this.getToDraw().size() - 1) - index;
			}

			Drawable drawable = this.getToDraw().get(i);
			if (drawable instanceof Interactable) {
				Interactable interactable = (Interactable) drawable;
				if (interactable.canFocus() || interactable.handleFocusCycle(forward)) {
					this.setFocused(interactable, index);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseHover(double mouseX, double mouseY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInside(v3f.getX(), v3f.getY()) && interactable.mouseHover(v3f.getX(), v3f.getY())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see RootContainer#setFocus(Drawable)
	 * @deprecated internal
	 */
	@Deprecated
	public void setFocused(@Nullable Interactable interactable, int index) {
		Interactable old = this.focused;
		this.focused = interactable;
		if (interactable != null) {
			interactable.onFocus();
			this.index = (index == -1 ? this.getToDraw().indexOf(interactable) : index) + 1;
		}

		if (old != null) {
			old.onLostFocus();
		}
	}

	@Nullable
	public Interactable getFocused() {
		return this.focused;
	}

	@Override
	public <T extends Drawable & Interactable> T drawableAt(double x, double y) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			transformation(interactable, v3f, x, y);
			if (((Drawable) interactable).getBounds().isInside(v3f.getX(), v3f.getY())) {
				if (interactable instanceof Container) {
					return ((Container) interactable).drawableAt(v3f.getX(), v3f.getY());
				} else if (interactable.mouseHover(v3f.getX(), v3f.getY())) {
					return (T) interactable;
				}
			}
		}

		return null;
	}

	@NotNull
	@Override
	public Iterator<Drawable> iterator() {
		return this.getToDraw().iterator();
	}

	public static void init() {}
}
