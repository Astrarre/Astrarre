package io.github.astrarre.gui.v0.api.panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;
import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.GuiPacketHandler;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.access.Container;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.rendering.v0.fabric.TransformationUtil;
import io.github.astrarre.stripper.Hide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Panel extends Drawable implements Interactable, Container {
	public static final int SYNC_CLIENT = 0;

	/**
	 * @param member the client to open the panel on
	 * todo text abstraction
	 */
	public static void openPanel(NetworkMember member, Panel panel, String nameTranslationKey) {
		member.send(GuiPacketHandler.OPEN_GUI, output -> {
			output.writeUTF(nameTranslationKey);
			panel.write(output);
		});
	}

	protected final List<Drawable> toDraw;
	protected int index;
	protected Interactable focused;

	public Panel(RootContainer rootContainer) {
		super(rootContainer, DrawableRegistry.PANEL);
		this.toDraw = new ArrayList<>();
	}

	public Panel(RootContainer rootContainer, Input input) {
		super(rootContainer, DrawableRegistry.PANEL);
		int size = input.readInt();
		this.toDraw = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			this.toDraw.add(((RootContainerInternal) rootContainer).forId(input.readInt()));
		}
	}

	@Override
	public void write0(Output output) {
		output.writeInt(this.toDraw.size());
		for (Drawable drawable : this.toDraw) {
			output.writeInt(drawable.getSyncId());
		}
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		for (Drawable drawable : this.toDraw) {
			drawable.render(graphics, tickDelta);
		}
	}

	@Override
	protected void receiveFromServer(int channel, Input input) {
		super.receiveFromServer(channel, input);
		if (channel == SYNC_CLIENT) {
			Drawable drawable = RootContainerInternal.readDrawable(this.rootContainer, input);
			this.toDraw.add(drawable);
		}
	}

	/**
	 * If called on the client, the method is ignored. the panel will wait until the server sends the component so it does not desync
	 *
	 * @see #addClient(Drawable)
	 */
	public void add(Drawable drawable) {
		if (!this.rootContainer.isClient()) {
			this.toDraw.add(drawable);
			this.sendToClient(SYNC_CLIENT, drawable::write);
		}
	}

	/**
	 * does not sync to the server, if the method is called on the server it is ignored
	 */
	public void addClient(Drawable drawable) {
		if (this.rootContainer.isClient()) {
			this.toDraw.add(drawable);
		}
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInsideProjection(v3f.getX(), v3f.getY())) {
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
		return (Iterable) Iterables.filter(
				this.toDraw,
				input -> input instanceof Interactable);
	}

	private static void transformation(Interactable interactable, Vector4f vector4f, double x, double y) {
		vector4f.set((float) x, (float) y, 1, 1);
		vector4f.transform(((Drawable)interactable).getInvertedMatrix());
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Vector4f v3f = new Vector4f(0, 0, 0, 1);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInsideProjection(v3f.getX(), v3f.getY()) && interactable.mouseClicked(v3f.getX(), v3f.getY(), button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInsideProjection(v3f.getX(), v3f.getY()) && interactable.mouseReleased(v3f.getX(), v3f.getY(), button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables()) {
			transformation(interactable, v3f, mouseX, mouseY);
			float mX = v3f.getX(), mY = v3f.getY();
			transformation(interactable, v3f, deltaX, deltaY);
			if (interactable.mouseReleased(mX, mY, button) && interactable.mouseDragged(mX, mY, button, v3f.getX(), v3f.getY())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInsideProjection(v3f.getX(), v3f.getY()) && interactable.mouseScrolled(v3f.getX(), v3f.getY(), amount)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handleFocusCycle(boolean forward) {
		if (this.focused != null && this.focused.handleFocusCycle(forward)) {
			return true;
		}

		for (int i = 0; i < this.toDraw.size(); i++) {
			int index = (i + this.index) % this.toDraw.size();
			if(!forward) {
				index = (this.toDraw.size() - 1) - index;
			}

			Drawable drawable = this.toDraw.get(i);
			if(drawable instanceof Interactable) {
				Interactable interactable = (Interactable) drawable;
				if(interactable.canFocus() || interactable.handleFocusCycle(forward)) {
					this.setFocused(interactable, index);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseHover(double mouseX, double mouseY) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables()) {
			Drawable drawable = (Drawable) interactable;
			transformation(interactable, v3f, mouseX, mouseY);
			if (drawable.getBounds().isInsideProjection(v3f.getX(), v3f.getY()) && interactable.mouseHover(v3f.getX(), v3f.getY())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(this.focused != null) {
			return this.focused.keyPressed(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if(this.focused != null) {
			return this.focused.keyReleased(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if(this.focused != null) {
			return this.focused.charTyped(chr, modifiers);
		}
		return false;
	}

	@Nullable
	public Interactable getFocused() {
		return this.focused;
	}

	/**
	 * @deprecated internal
	 * @see RootContainer#setFocus(Drawable)
	 */
	@Hide
	@Deprecated
	public void setFocused(@Nullable Interactable interactable, int index) {
		Interactable old = this.focused;
		this.focused = interactable;
		if(interactable != null) {
			interactable.onFocus();
			this.index = (index == -1 ? this.toDraw.indexOf(interactable) : index) + 1;
		}

		if(old != null) {
			old.onLostFocus();
		}
	}

	@Override
	public void setTransformation(Transformation transformation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBounds(Polygon polygon) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Drawable & Interactable> T drawableAt(double x, double y) {
		Vector4f v3f = new Vector4f(0, 0, 0, 0);
		for (Interactable interactable : this.interactables()) {
			transformation(interactable, v3f, x, y);
			if(((Drawable)interactable).getBounds().isInsideProjection(v3f.getX(), v3f.getY())) {
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
		return this.toDraw.iterator();
	}
}
