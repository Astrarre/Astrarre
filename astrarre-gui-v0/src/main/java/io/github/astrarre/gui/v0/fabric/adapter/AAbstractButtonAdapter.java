package io.github.astrarre.gui.v0.fabric.adapter;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import net.minecraft.client.gui.widget.ClickableWidget;

public abstract class AAbstractButtonAdapter<T extends ClickableWidget> extends AElementAdapter<T> {
	public static final int LOCK = 0;
	protected boolean enabled = true;
	private final int width, height;

	public AAbstractButtonAdapter(DrawableRegistry.Entry id, int width, int height) {
		super(id);
		this.width = width;
		this.height = height;
		this.setBounds(Polygon.rectangle(width, height));
	}

	protected AAbstractButtonAdapter(DrawableRegistry.Entry id, NBTagView input) {
		this(id, input.getInt("width"), input.getInt("height"));
		this.enabled = input.getBool("enabled");
	}

	protected void onActiveClient(boolean isActive) {
		this.drawable.active = isActive;
	}

	@Override
	protected void receiveFromServer(RootContainer container, int channel, NBTagView input) {
		if (channel == LOCK) {
			this.onActiveClient(input.getBool("enabled"));
		}
		super.receiveFromServer(container, channel, input);
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		output.putInt("width", this.width);
		output.putInt("height",this.height);
		output.putBool("enabled", this.enabled);
	}

	public boolean isEnabled() {
		if (this.isClient() && this.drawable != null) {
			return this.drawable.active;
		}
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.isClient()) {
			this.onActiveClient(enabled);
		} else {
			this.sendToClients(LOCK, NBTagView.builder().putBool("enabled", enabled));
		}
		this.enabled = enabled;
	}
}
