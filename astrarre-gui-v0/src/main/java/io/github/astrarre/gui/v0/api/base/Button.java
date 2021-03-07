package io.github.astrarre.gui.v0.api.base;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Button extends Drawable implements Interactable {
	private static final DrawableRegistry.Entry BUTTON = DrawableRegistry.register(Id.newInstance("astrarre-gui-v0", "button"), Button::new);
	private static final Texture BEACON = new Texture("minecraft", "textures/gui/container/beacon.png", 256, 256);

	public static final Polygon SQUARE_22x22 = new Polygon.Builder(4)
			.addVertex(0, 0)
			.addVertex(0, 22)
			.addVertex(22, 22)
			.addVertex(22, 0)
			.build();

	/**
	 * the channel id for button toggling
	 */
	public static final int ON_CLICK = 0;


	@Environment(EnvType.CLIENT)
	protected boolean pressed;
	@Environment(EnvType.CLIENT)
	protected boolean highlighted;

	/**
	 * the number of times the button has been clicked. This is updated on the server and client
	 */
	public int clickCount;

	protected Button(RootContainer rootContainer, DrawableRegistry.Entry id) {
		super(rootContainer, id);
	}

	public Button(RootContainer rootContainer) {
		super(rootContainer, BUTTON);
		this.setBounds(SQUARE_22x22);
	}


	public Button(RootContainer rootContainer, Input input) {
		this(rootContainer);
	}

	/**
	 * called when the button is clicked on the clientside, sends a packet to the server.
	 * If you're using this as a clientside only button, override this method and don't call super.
	 *
	 * If you are serializing the component to the client, you must register your overriden class
	 */
	@Environment(EnvType.CLIENT)
	protected void clickedClient() {
		this.sendToServer(ON_CLICK, o -> {});
		this.clickCount++;
	}

	/**
	 * if just overriding this method, you technically don't need to register it since this logic wont be called on the client anyways.
	 */
	protected void clickedServer() {
		this.clickCount++;
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		if(this.pressed) {
			graphics.drawTexture(BEACON, 22, 219, 22, 22);
		} else if(this.highlighted){
			graphics.drawTexture(BEACON, 66, 219, 22, 22);
		} else {
			graphics.drawTexture(BEACON, 0, 219, 22, 22);
		}
		this.highlighted = false;
	}

	@Override
	protected void write0(Output output) {}

	@Override
	protected void receiveFromClient(NetworkMember member, int channel, Input input) {
		super.receiveFromClient(member, channel, input);
		if(channel == ON_CLICK) {
			this.clickedServer();
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.pressed = true;
		return true;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.pressed = false;
		this.clickedClient();
		return true;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean mouseHover(double mouseX, double mouseY) {
		this.highlighted = true;
		return true;
	}

	public static void init() {}
}
