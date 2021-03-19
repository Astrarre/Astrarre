package io.github.astrarre.gui.v0.api.base.widgets;

import java.util.ArrayList;
import java.util.List;

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
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class AButton extends Drawable implements Interactable {
	public static final Data MEDIUM = new Data(
			AstrarreIcons.MEDIUM_BUTTON_ACTIVE,
			AstrarreIcons.MEDIUM_BUTTON_HIGHLIGHTED,
			AstrarreIcons.MEDIUM_BUTTON_PRESSED,
			AstrarreIcons.MEDIUM_BUTTON_DISABLED);
	public static final Data ARROW_UP = new Data(AstrarreIcons.UP_ARROW_ACTIVE, null, AstrarreIcons.UP_ARROW_PRESSED, null);
	public static final Data ARROW_DOWN = new Data(AstrarreIcons.DOWN_ARROW_ACTIVE, null, AstrarreIcons.DOWN_ARROW_PRESSED, null);
	public static final Data ARROW_LEFT = new Data(AstrarreIcons.LEFT_ARROW_ACTIVE, null, AstrarreIcons.LEFT_ARROW_PRESSED, null);
	public static final Data ARROW_RIGHT = new Data(AstrarreIcons.RIGHT_ARROW_ACTIVE, null, AstrarreIcons.RIGHT_ARROW_PRESSED, null);
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "button"), AButton::new);
	private static final int DISABLE = 0;
	private static final int PRESSED = 1;
	protected final List<Runnable> onPress = new ArrayList<>();
	protected final Data part;
	protected boolean highlight, pressed, disabled;

	public AButton(Data part) {
		this(ENTRY, part);
	}

	protected AButton(DrawableRegistry.Entry id, Data part) {
		super(id);
		this.part = part;
		this.setBounds(Polygon.rectangle(this.width(), this.height()));
	}

	public float width() {
		return this.part.active.width;
	}

	public float height() {
		return this.part.active.height;
	}

	protected AButton(DrawableRegistry.Entry entry, Input input) {
		super(entry);
		this.part = readData(input);
		this.disabled = input.readBoolean();
	}

	/**
	 * add a listener, if on the client, only listens on the client, if on the server, only listens on the server
	 */
	public void onPress(Runnable runnable) {
		this.onPress.add(runnable);
	}

	public static void init() {}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		if (this.disabled) {
			this.drawDisabled(container, graphics, tickDelta);
		} else if (this.pressed) {
			this.drawPressed(container, graphics, tickDelta);
		} else if (this.highlight) {
			this.drawHighlighted(container, graphics, tickDelta);
		} else {
			this.drawActive(container, graphics, tickDelta);
		}
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		writeData(this.part, output);
		output.writeBoolean(this.disabled);
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
		if (channel == PRESSED) {
			this.onPressServer();
		}
	}

	/**
	 * fired when the button is pressed on the server, if overriden you don't need to register the class
	 */
	public void onPressServer() {
		this.onPress.forEach(Runnable::run);
	}

	protected void drawDisabled(RootContainer container, Graphics3d graphics, float tickDelta) {
		if (this.part == null || this.part.disabled == null) {
			this.drawPressed(container, graphics, tickDelta);
			graphics.fillRect(this.width(), this.height(), 0xaa000000);
		} else {
			graphics.drawTexture(this.part.disabled);
		}
	}

	protected void drawPressed(RootContainer container, Graphics3d graphics, float tickDelta) {
		if (this.part == null || this.part.pressed == null) {
			this.drawActive(container, graphics, tickDelta);
			graphics.fillRect(this.width(), this.height(), 0xaa000000);
		} else {
			graphics.drawTexture(this.part.pressed);
		}
	}

	protected void drawHighlighted(RootContainer container, Graphics3d graphics, float tickDelta) {
		if (this.part == null || this.part.highlighted == null) {
			this.drawActive(container, graphics, tickDelta);
			graphics.fillRect(this.width(), this.height(), 0xaaffffff);
		} else {
			graphics.drawTexture(this.part.highlighted);
		}
	}

	protected void drawActive(RootContainer container, Graphics3d graphics, float tickDelta) {
		graphics.drawTexture(this.part.active);
	}

	@Override
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		this.pressed = true;
		return true;
	}

	@Override
	public boolean mouseReleased(RootContainer container, double mouseX, double mouseY, int button) {
		if (this.pressed) {
			this.onPressClient();
		}
		this.pressed = false;
		return true;
	}

	/**
	 * fired when the button is pressed on the client, if serializing you must register the class
	 */
	@Environment (EnvType.CLIENT)
	public void onPressClient() {
		this.sendToServer(PRESSED, o -> {});
		this.onPress.forEach(Runnable::run);
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	@Override
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		this.highlight = true;
	}

	@Override
	public void onLoseHover(RootContainer container) {
		this.pressed = false;
		this.highlight = false;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
		this.sendToClients(DISABLE, output -> output.writeBoolean(disabled));
	}

	public final static class Data {
		public final TexturePart active, pressed;
		@Nullable public final TexturePart highlighted, disabled;

		public Data() {
			this(null, null, null, null);
		}

		public Data(TexturePart active, @Nullable TexturePart highlighted, TexturePart pressed, @Nullable TexturePart disabled) {
			this.active = active;
			this.highlighted = highlighted;
			this.pressed = pressed;
			this.disabled = disabled;
		}

		public Data withActive(TexturePart part) {
			return new Data(part, this.highlighted, this.pressed, this.disabled);
		}

		public Data withHighlighted(@Nullable TexturePart part) {
			return new Data(this.active, part, this.pressed, this.disabled);
		}

		public Data withPressed(TexturePart part) {
			return new Data(this.active, this.highlighted, part, this.disabled);
		}

		public Data withDisabled(TexturePart part) {
			return new Data(this.active, this.highlighted, this.pressed, part);
		}
	}

	public static Data readData(Input input) {
		return new Data(readPart(input), readPart(input), readPart(input), readPart(input));
	}

	public static TexturePart readPart(Input input) {
		if (input.readBoolean()) {
			return null;
		}

		return new TexturePart(readTexture(input), input.readInt(), input.readInt(), input.readInt(), input.readInt());
	}


	public static Texture readTexture(Input input) {
		return new Texture(input.readId(), input.readInt(), input.readInt());
	}

	public static void writeData(Data data, Output output) {
		writePart(data.active, output);
		writePart(data.highlighted, output);
		writePart(data.pressed, output);
		writePart(data.disabled, output);
	}

	public static void writePart(TexturePart part, Output output) {
		output.writeBoolean(part == null);
		if (part != null) {
			write(part.texture, output);
			output.writeInt(part.offX);
			output.writeInt(part.offY);
			output.writeInt(part.height);
			output.writeInt(part.width);
		}
	}

	public static void write(Texture texture, Output output) {
		output.writeId(texture.getId());
		output.writeInt(texture.getHeight());
		output.writeInt(texture.getWidth());
	}
}
