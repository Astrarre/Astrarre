package io.github.astrarre.gui.v0.api.drawable;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.internal.util.MatrixGraphicsUtil;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Polygon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Button extends Drawable implements Interactable {
	public static final Texture UNPRESS = new Texture("minecraft", "textures/gui/container/beacon.png", 256, 256);
	public static final Texture PRESSED = new Texture("minecraft", "textures/gui/container/beacon.png", 256, 256);
	public static final Polygon SQUARE_18x18 = new Polygon.Builder(4)
			.addVertex(0, 0, 0)
			.addVertex(0, 22, 0)
			.addVertex(22, 22, 0)
			.addVertex(22, 0, 0)
			.build();

	/**
	 * the channel id for button toggling
	 */
	public static final int BUTTON_TOGGLE = 0;
	protected boolean state;

	@Environment(EnvType.CLIENT)
	protected boolean highlighted;

	public Button(RootContainer rootContainer) {
		super(rootContainer, DrawableRegistry.BUTTON);
		this.setBounds(SQUARE_18x18);
	}

	public Button(RootContainer rootContainer, Input input) {
		this(rootContainer);
		this.state = input.readBoolean();
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		if(this.state) {
			graphics.drawTexture(PRESSED, 0, 219, 22, 241);
		} else {
			graphics.drawTexture(UNPRESS, 22, 219, 44, 241);
		}
	}

	@Override
	public void write0(Output output) {
		output.writeBoolean(this.state);
	}

	@Override
	protected void receiveFromServer(int channel, Input input) {
		super.receiveFromServer(channel, input);
		if(channel == BUTTON_TOGGLE) {
			this.state = input.readBoolean();
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.state ^= true;
		this.sendToServer(BUTTON_TOGGLE, o -> o.writeBoolean(this.state));
		return true;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseHover(double mouseX, double mouseY) {
		this.highlighted = true;
		return true;
	}
}
