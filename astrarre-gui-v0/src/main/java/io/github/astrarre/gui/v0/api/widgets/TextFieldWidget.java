package io.github.astrarre.gui.v0.api.widgets;

import java.nio.charset.StandardCharsets;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.fabric.adapter.AbstractButtonAdapter;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TextFieldWidget extends AbstractButtonAdapter<net.minecraft.client.gui.widget.TextFieldWidget> {
	public static final int UPDATE_TEXT = 1;
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "text_field"), TextFieldWidget::new);
	// only exists on the server
	private String text = "";

	public TextFieldWidget(int width, int height) {
		super(ENTRY, width, height);
	}

	@Environment(EnvType.CLIENT)
	private TextFieldWidget(Input input) {
		this(ENTRY, input);
	}

	protected TextFieldWidget(DrawableRegistry.Entry id, int width, int height) {
		super(id, width, height);
	}

	@Environment(EnvType.CLIENT)
	protected TextFieldWidget(DrawableRegistry.Entry id, Input input) {
		super(id, input);
		this.text = input.readUTF();
		Polygon enclosing = this.getBounds().getEnclosing();
		this.drawable = new net.minecraft.client.gui.widget.TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, (int)enclosing.getX(2), (int)enclosing.getY(2), null);
		this.drawable.active = this.enabled;
		this.drawable.setEditable(this.isEnabled());
		this.drawable.setChangedListener(this::sendTextToServer);
		this.drawable.setTextPredicate(this::sanitize);
	}

	@Override
	protected void onActiveClient(boolean isActive) {
		super.onActiveClient(isActive);
		this.drawable.setEditable(isActive);
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		super.write0(container, output);
		String text = this.getText();
		output.writeUTF(text);
	}

	public String getText() {
		if(this.isClient() && this.drawable != null) {
			return this.drawable.getText();
		}
		return this.text;
	}

	protected boolean sanitize(@Nullable String text) {
		return text != null && StandardCharsets.US_ASCII.newEncoder().canEncode(text);
	}

	protected void sendTextToServer(String text) {
		this.sendToServer(UPDATE_TEXT, o -> o.writeUTF(text));
	}

	public boolean setText(String text) {
		if(this.sanitize(text)) {
			if(this.isClient()) {
				this.drawable.setText(text);
				this.sendTextToServer(text);
			} else {
				this.text = text;
				this.sendToClients(UPDATE_TEXT, o -> o.writeUTF(text));
			}
			return true;
		}
		return false;
	}

	protected void syncedFromClient(String string) {
		this.text = string;
	}

	@Override
	protected void receiveFromClient(RootContainer container, NetworkMember member, int channel, Input input) {
		if(channel == UPDATE_TEXT) {
			String str = input.readUTF();
			if(this.sanitize(str)) {
				this.syncedFromClient(str);
			}
		}
		super.receiveFromClient(container, member, channel, input);
	}

	@Override
	protected void receiveFromServer(RootContainer container, int channel, Input input) {
		if(channel == UPDATE_TEXT) {
			this.drawable.setText(this.text = input.readUTF());
		}
		super.receiveFromServer(container, channel, input);
	}

	@Override
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		container.setFocus(this);
		return super.mouseClicked(container, mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(RootContainer container, int keyCode, int scanCode, int modifiers) {
		super.keyPressed(container, keyCode, scanCode, modifiers);
		return true;
	}

	public static void init() {}
}
