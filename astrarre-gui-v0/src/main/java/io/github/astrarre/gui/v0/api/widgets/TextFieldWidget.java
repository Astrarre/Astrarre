package io.github.astrarre.gui.v0.api.widgets;

import java.nio.charset.StandardCharsets;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.fabric.adapter.AbstractButtonAdapter;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;

public class TextFieldWidget extends AbstractButtonAdapter<net.minecraft.client.gui.widget.TextFieldWidget> {
	public static final int UPDATE_TEXT = 1;
	private static final DrawableRegistry.Entry TEXT_FIELD = DrawableRegistry.register(Id.create("astrarre-gui-v0", "text_field"), TextFieldWidget::new);
	// only exists on the server
	private String text = "";

	public TextFieldWidget(int width, int height) {
		super(TEXT_FIELD, width, height);
	}

	public TextFieldWidget(Input input) {
		super(TEXT_FIELD, input);
		this.text = input.readUTF();
		this.drawable = new net.minecraft.client.gui.widget.TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, this.getWidth(), this.getHeight(), null);
		this.drawable.active = this.enabled;
		this.drawable.setEditable(this.isEnabled());
		this.drawable.setChangedListener(s -> this.sendToServer(UPDATE_TEXT, o -> o.writeUTF(s)));
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

	public boolean setText(String text) {
		if(this.sanitize(text)) {
			if(this.isClient()) {
				this.drawable.setText(text);
				this.sendToServer(UPDATE_TEXT, o -> o.writeUTF(text));
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
				// todo update to all other viewers
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
