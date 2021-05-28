package io.github.astrarre.gui.v0.api.base.widgets;

import java.nio.charset.StandardCharsets;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.fabric.adapter.AAbstractButtonAdapter;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ATextFieldWidget extends AAbstractButtonAdapter<TextFieldWidget> {
	public static final int UPDATE_TEXT = 1;
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "text_field"), ATextFieldWidget::new);
	// only exists on the server
	private String text = "";

	public ATextFieldWidget(int width, int height) {
		super(ENTRY, width, height);
	}

	@Environment(EnvType.CLIENT)
	private ATextFieldWidget(NBTagView input) {
		this(ENTRY, input);
	}

	protected ATextFieldWidget(DrawableRegistry.Entry id, int width, int height) {
		super(id, width, height);
	}

	@Environment(EnvType.CLIENT)
	protected ATextFieldWidget(DrawableRegistry.Entry id, NBTagView input) {
		super(id, input);
		this.text = input.getString("text");
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		if(container.isClient()) {
			Polygon enclosing = this.getBounds().getEnclosing();
			this.drawable = new net.minecraft.client.gui.widget.TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, (int)enclosing.getX(2), (int)enclosing.getY(2), null);
			this.drawable.setText(this.text);
			this.drawable.active = this.enabled;
			this.drawable.setEditable(this.isEnabled());
			this.drawable.setChangedListener(this::sendTextToServer);
			this.drawable.setTextPredicate(this::sanitize);
		}
	}

	@Override
	protected void onActiveClient(boolean isActive) {
		super.onActiveClient(isActive);
		this.drawable.setEditable(isActive);
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		super.write0(container, output);
		output.putString("text", this.getText());
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
		this.sendToServer(UPDATE_TEXT, NBTagView.builder().putString("text", text));
	}

	public boolean setText(String text) {
		if(this.sanitize(text)) {
			if(this.isClient()) {
				this.drawable.setText(text);
				this.sendTextToServer(text);
			} else {
				this.text = text;
				this.sendToClients(UPDATE_TEXT, NBTagView.builder().putString("text", text));
			}
			return true;
		}
		return false;
	}

	protected void syncedFromClient(String string) {
		this.text = string;
	}

	@Override
	protected void receiveFromClient(RootContainer container, NetworkMember member, int channel, NBTagView input) {
		if(channel == UPDATE_TEXT) {
			String str = input.getString("text");
			if(this.sanitize(str)) {
				this.syncedFromClient(str);
			}
		}
		super.receiveFromClient(container, member, channel, input);
	}

	@Override
	protected void receiveFromServer(RootContainer container, int channel, NBTagView input) {
		if(channel == UPDATE_TEXT) {
			this.drawable.setText(this.text = input.getString("text"));
		}
		super.receiveFromServer(container, channel, input);
	}

	@Override
	public boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		if(this.enabled) {
			container.setFocus(this);
		}
		return super.mouseClicked(container, mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(RootContainer container, int keyCode, int scanCode, int modifiers) {
		super.keyPressed(container, keyCode, scanCode, modifiers);
		return true;
	}

	public static void init() {}

	@Override
	public void tick(RootContainer container) {
		this.drawable.tick();
	}
}
