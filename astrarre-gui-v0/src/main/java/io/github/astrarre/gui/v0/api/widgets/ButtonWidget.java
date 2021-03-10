package io.github.astrarre.gui.v0.api.widgets;

import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.textures.TexturePart;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ButtonWidget extends Drawable implements Interactable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "button"), ButtonWidget::new);
	public static final Data MEDIUM = new Data(
			AstrarreIcons.MEDIUM_BUTTON_ACTIVE,
			AstrarreIcons.MEDIUM_BUTTON_HIGHLIGHTED,
			AstrarreIcons.MEDIUM_BUTTON_PRESSED,
			AstrarreIcons.MEDIUM_BUTTON_DISABLED);

	private static final int DISABLE = 0;
	private static final int PRESSED = 1;
	protected final Data part;
	protected boolean highlight, pressed, disabled;

	public ButtonWidget(Data part) {
		this(ENTRY, part);
	}

	protected ButtonWidget(DrawableRegistry.Entry id, Data part) {
		super(id);
		this.part = part;
		this.setBounds(Polygon.rectangle(part.active.width, part.active.height));
	}

	public ButtonWidget(DrawableRegistry.Entry entry, Input input) {
		super(entry);
		this.part = readData(input);
		this.disabled = input.readBoolean();
	}

	/**
	 * fired when the button is pressed on the server, if overriden you don't need to register the class
	 */
	public void onPressServer() {}

	/**
	 * fired when the button is pressed on the client, if serializing you must register the class
	 */
	@Environment(EnvType.CLIENT)
	public void onPressClient() {
		this.sendToServer(PRESSED, o -> {});
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		if (this.disabled) {
			graphics.drawTexture(this.part.disabled);
		} else if (this.pressed) {
			graphics.drawTexture(this.part.pressed);
		} else if (this.highlight) {
			graphics.drawTexture(this.part.highlighted);
		} else {
			graphics.drawTexture(this.part.active);
		}
	}

	@Override
	protected void receiveFromServer(RootContainer container, int channel, Input input) {
		super.receiveFromServer(container, channel, input);
		if (channel == DISABLE) {
			this.disabled = input.readBoolean();
		}
	}

	@Override
	protected void receiveFromClient(RootContainer container, NetworkMember member, int channel, Input input) {
		super.receiveFromClient(container, member, channel, input);
		if(channel == PRESSED) {
			this.onPressServer();
		}
	}

	@Override
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		this.pressed = true;
		return true;
	}

	@Override
	public boolean mouseReleased(RootContainer container, double mouseX, double mouseY, int button) {
		if(this.pressed) {
			this.onPressClient();
		}
		this.pressed = false;
		return true;
	}

	@Override
	public void onLoseHover(RootContainer container) {
		this.pressed = false;
		this.highlight = false;
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	@Override
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		this.highlight = true;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
		this.sendToClients(DISABLE, output -> output.writeBoolean(disabled));
	}

	public final static class Data {
		public final TexturePart active, highlighted, pressed, disabled;

		public Data() {
			this(null, null, null, null);
		}

		public Data(TexturePart active, TexturePart highlighted, TexturePart pressed, TexturePart disabled) {
			this.active = active;
			this.highlighted = highlighted;
			this.pressed = pressed;
			this.disabled = disabled;
		}

		public Data withActive(TexturePart part) {
			return new Data(part, this.highlighted, this.pressed, this.disabled);
		}

		public Data withHighlighted(TexturePart part) {
			return new Data(this.active, part, this.pressed, this.disabled);
		}

		public Data withPressed(TexturePart part) {
			return new Data(this.active, this.highlighted, part, this.disabled);
		}

		public Data withDisabled(TexturePart part) {
			return new Data(this.active, this.highlighted, this.pressed, part);
		}
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		writeData(this.part, output);
		output.writeBoolean(this.disabled);
	}

	public static Data readData(Input input) {
		return new Data(readPart(input), readPart(input), readPart(input), readPart(input));
	}

	public static TexturePart readPart(Input input) {
		return new TexturePart(readTexture(input), input.readInt(), input.readInt(), input.readInt(), input.readInt());
	}

	public static Texture readTexture(Input input) {
		return new Texture(input.readId(), input.readInt(), input.readInt());
	}

	public static void init() {}

	public static void writeData(Data data, Output output) {
		writePart(data.active, output);
		writePart(data.highlighted, output);
		writePart(data.pressed, output);
		writePart(data.disabled, output);
	}

	public static void writePart(TexturePart part, Output output) {
		write(part.texture, output);
		output.writeInt(part.offX);
		output.writeInt(part.offY);
		output.writeInt(part.height);
		output.writeInt(part.width);
	}

	public static void write(Texture texture, Output output) {
		output.writeId(texture.getId());
		output.writeInt(texture.getHeight());
		output.writeInt(texture.getWidth());
	}
}
