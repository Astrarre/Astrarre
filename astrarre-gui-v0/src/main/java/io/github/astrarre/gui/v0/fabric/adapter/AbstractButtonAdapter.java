package io.github.astrarre.gui.v0.fabric.adapter;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.util.Polygon;

import net.minecraft.client.gui.widget.AbstractButtonWidget;

public abstract class AbstractButtonAdapter<T extends AbstractButtonWidget> extends ElementAdapter<T> {
	public static final int LOCK = 0;
	protected boolean enabled = true;
	private final int width, height;

	public AbstractButtonAdapter(RootContainer rootContainer, DrawableRegistry.Entry id, int width, int height) {
		super(rootContainer, id);
		this.width = width;
		this.height = height;
		this.setBoundsProtected(new Polygon.Builder(4).addVertex(0, 0).addVertex(0, height).addVertex(width, height).addVertex(width, 0).build());
	}

	protected AbstractButtonAdapter(RootContainer container, DrawableRegistry.Entry id, Input input) {
		this(container, id, input.readInt(), input.readInt());
		this.enabled = input.readBoolean();
	}

	protected void onActiveClient(boolean isActive) {
		this.drawable.active = isActive;
	}

	@Override
	protected void receiveFromServer(int channel, Input input) {
		if (channel == LOCK) {
			this.onActiveClient(input.readBoolean());
		}
		super.receiveFromServer(channel, input);
	}

	@Override
	protected void write0(Output output) {
		output.writeInt(this.width);
		output.writeInt(this.height);
		output.writeBoolean(this.enabled);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public boolean isEnabled() {
		if (this.rootContainer.isClient() && this.drawable != null) {
			return this.drawable.active;
		}
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.rootContainer.isClient()) {
			this.onActiveClient(enabled);
		} else {
			this.sendToClients(LOCK, output -> output.writeBoolean(enabled));
		}
		this.enabled = enabled;
	}
}
