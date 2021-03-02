package io.github.astrarre.gui.v0.api;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.DrawableInternal;
import io.github.astrarre.gui.internal.GuiPacketHandler;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.ApiStatus;

public abstract class Drawable extends DrawableInternal {
	/**
	 * @see DrawableRegistry#register(Id, BiFunction)
	 */
	public final DrawableRegistry.Entry registryId;
	private Polygon untransformedBounds = Polygon.EMPTY;
	private Transformation transformation = Transformation.EMPTY;
	private Polygon bounds = Polygon.EMPTY;

	public Drawable(RootContainer rootContainer, DrawableRegistry.Entry id) {
		super(rootContainer);
		this.registryId = id;
	}



	public final void render(Graphics3d graphics, float tickDelta) {
		try (Close close = graphics.applyTransformation(this.transformation)) {
			this.render0(graphics, tickDelta);
		}
	}

	protected abstract void render0(Graphics3d graphics, float tickDelta);

	/**
	 * called when the drawable is removed
	 */
	public void remove() {}

	/**
	 * @throws UnsupportedOperationException if this is a clientside component only and cannot be sent from the server
	 */
	public final void write(Output output) {
		output.writeId(this.registryId.id);
		output.writeInt(this.getSyncId());
		this.write0(output);
	}

	public static Drawable read(RootContainer rootContainer, Input input) {
		return RootContainerInternal.readDrawable(rootContainer, input);
	}

	protected abstract void write0(Output output);

	/**
	 * send a packet to the client side counterparts of this drawable
	 *
	 * @param channel an internal channel id for this specific drawable
	 */
	public final void sendToClient(int channel, Consumer<Output> consumer) {
		for (NetworkMember viewer : this.rootContainer.getViewers()) {
			viewer.send(GuiPacketHandler.DRAWABLE_PACKET_CHANNEL, output -> {
				output.writeInt(channel);
				output.writeEnum(this.rootContainer.getType());
				output.writeInt(this.getSyncId());
				consumer.accept(output);
			});
		}
	}

	/**
	 * send a packet to the server side counterpart of this drawable
	 *
	 * @param channel an internal channel id for this specific drawable
	 * @see #receiveFromServer(int, Input)
	 */
	public final void sendToServer(int channel, Consumer<Output> consumer) {
		ModPacketHandler.INSTANCE.sendToServer(GuiPacketHandler.DRAWABLE_PACKET_CHANNEL, output -> {
			output.writeInt(channel);
			output.writeEnum(this.rootContainer.getType());
			output.writeInt(this.getSyncId());
			consumer.accept(output);
		});
	}

	/**
	 * called when a drawable-specific packet is recieved from the server.
	 *
	 * @see #sendToClient(int, Consumer)
	 */
	@Override
	@ApiStatus.OverrideOnly
	protected void receiveFromServer(int channel, Input input) {
	}

	@Override
	@ApiStatus.OverrideOnly
	protected void receiveFromClient(NetworkMember member, int channel, Input input) {
	}

	public Transformation getTransformation() {
		return this.transformation;
	}

	public void setTransformation(Transformation transformation) {
		if (transformation == Transformation.EMPTY) {
			this.bounds = this.untransformedBounds;
		} else {
			this.bounds = this.untransformedBounds.toBuilder().transform(transformation).build();
		}
		this.transformation = transformation;
	}

	/**
	 * @return the post-transformed bounds of this drawable
	 */
	public Polygon getBounds() {
		return this.bounds;
	}

	public void setBounds(Polygon polygon) {
		this.untransformedBounds = polygon;
		if (this.transformation == Transformation.EMPTY) {
			this.bounds = polygon;
		} else {
			this.bounds = polygon.toBuilder().transform(this.transformation).build();
		}
	}
}
