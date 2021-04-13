package io.github.astrarre.gui.v0.api.base.widgets;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.Serializable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class AButton extends ADrawable implements Interactable {
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

	protected AButton(DrawableRegistry.Entry entry, NBTagView input) {
		super(entry);
		this.part = SERIALIZER.read(input.getValue("part"));
		this.disabled = input.getBool("disabled");
	}

	public float width() {
		return this.part.active.width;
	}

	public float height() {
		return this.part.active.height;
	}

	/**
	 * add a listener, if on the client, only listens on the client, if on the server, only listens on the server
	 */
	public void onPress(Runnable runnable) {
		this.onPress.add(runnable);
	}

	public static void init() {}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
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
	protected void write0(RootContainer container, NBTagView.Builder output) {
		if(this.part != null) {
			output.putSerializable("part", this.part);
		}
		output.putBool("disabled", this.disabled);
	}

	@Override
	protected void receiveFromServer(RootContainer container, int channel, NBTagView input) {
		super.receiveFromServer(container, channel, input);
		if (channel == DISABLE) {
			this.disabled = input.getBool("disabled");
		}
	}

	@Override
	protected void receiveFromClient(RootContainer container, NetworkMember member, int channel, NBTagView input) {
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

	protected void drawDisabled(RootContainer container, GuiGraphics graphics, float tickDelta) {
		if (this.part == null || this.part.disabled == null) {
			this.drawPressed(container, graphics, tickDelta);
			graphics.fillRect(this.width(), this.height(), 0xaa000000);
		} else {
			graphics.drawSprite(this.part.disabled);
		}
	}

	protected void drawPressed(RootContainer container, GuiGraphics graphics, float tickDelta) {
		if (this.part == null || this.part.pressed == null) {
			this.drawActive(container, graphics, tickDelta);
			graphics.fillRect(this.width(), this.height(), 0xaa000000);
		} else {
			graphics.drawSprite(this.part.pressed);
		}
	}

	protected void drawHighlighted(RootContainer container, GuiGraphics graphics, float tickDelta) {
		if (this.part == null || this.part.highlighted == null) {
			this.drawActive(container, graphics, tickDelta);
			graphics.fillRect(this.width(), this.height(), 0xaaffffff);
		} else {
			graphics.drawSprite(this.part.highlighted);
		}
	}

	protected void drawActive(RootContainer container, GuiGraphics graphics, float tickDelta) {
		graphics.drawSprite(this.part.active);
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
		this.sendToServer(PRESSED, NBTagView.EMPTY);
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
		this.sendToClients(DISABLE, NBTagView.builder().putBool("disabled", disabled));
	}

	public static final Serializer<Data> SERIALIZER = Serializer.of(Data::new);
	public final static class Data implements Serializable {
		public final Sprite.Sized active;
		@Nullable public final Sprite.Sized highlighted, disabled, pressed;

		public Data() {
			this(null, null, null, null);
		}

		public Data(Sprite.Sized active, @Nullable Sprite.Sized highlighted, @Nullable Sprite.Sized pressed, @Nullable Sprite.Sized disabled) {
			this.active = active;
			this.highlighted = highlighted;
			this.pressed = pressed;
			this.disabled = disabled;
		}

		protected Data(NbtValue value) {
			NBTagView tag = value.asTag();
			if(tag.get("highlighted") != null) {
				this.highlighted = Sprite.SIZED_SER.read(tag.getValue("highlighted"));
			} else this.highlighted = null;
			if(tag.get("pressed") != null) {
				this.pressed = Sprite.SIZED_SER.read(tag.getValue("pressed"));
			} else this.pressed = null;
			if(tag.get("disabled") != null) {
				this.disabled = Sprite.SIZED_SER.read(tag.getValue("disabled"));
			} else this.disabled = null;

			this.active = Sprite.SIZED_SER.read(tag.getValue("active"));
		}

		public Data withActive(Sprite.Sized part) {
			return new Data(part, this.highlighted, this.pressed, this.disabled);
		}

		public Data withHighlighted(@Nullable Sprite.Sized part) {
			return new Data(this.active, part, this.pressed, this.disabled);
		}

		public Data withPressed(Sprite.Sized part) {
			return new Data(this.active, this.highlighted, part, this.disabled);
		}

		public Data withDisabled(Sprite.Sized part) {
			return new Data(this.active, this.highlighted, this.pressed, part);
		}

		@Override
		public NbtValue save() {
			NBTagView.Builder builder = NBTagView.builder();
			if(this.highlighted != null) {
				builder.putSerializable("highlighted", this.highlighted);
			}
			if(this.disabled != null) {
				builder.putSerializable("disabled", this.disabled);
			}
			if(this.pressed != null) {
				builder.putSerializable("pressed", this.pressed);
			}

			builder.putSerializable("active", this.active);
			return builder;
		}
	}
}
