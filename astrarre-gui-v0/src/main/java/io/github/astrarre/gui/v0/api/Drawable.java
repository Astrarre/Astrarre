package io.github.astrarre.gui.v0.api;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.github.astrarre.gui.internal.GuiUtil;
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

import net.minecraft.util.math.Matrix4f;

public abstract class Drawable extends DrawableInternal {
	public static final int BOUNDS_CHANGE = -1;
	public static final int TRANSFORM_CHANGE = -2;

	/**
	 * @see DrawableRegistry#register(Id, BiFunction)
	 */
	public final DrawableRegistry.Entry registryId;
	Transformation transformation = Transformation.EMPTY;
	Matrix4f invertedMatrix;
	Polygon bounds = Polygon.EMPTY;

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
		GuiUtil.write(this.bounds, output);
		GuiUtil.write(this.transformation, output);
	}

	public static Drawable read(RootContainer rootContainer, Input input) {
		Id id = input.readId();
		BiFunction<RootContainer, Input, Drawable> function = DrawableRegistry.forId(id);
		if (function == null || input.bytes() < 4) {
			throw new IllegalStateException("Broken (d/s)erializer! " + id);
		} else {
			int syncId = input.readInt();
			Drawable drawable = function.apply(rootContainer, input);
			GuiUtil.setSyncId(drawable, syncId);
			drawable.bounds = GuiUtil.readPolygon(input);
			drawable.transformation = GuiUtil.readTransformation(input);
			drawable.invertedMatrix = null;
			return drawable;
		}
	}

	protected abstract void write0(Output output);

	/**
	 * send a packet to the client side counterparts of this drawable
	 *
	 * @param channel an internal channel id for this specific drawable
	 */
	public final void sendToClients(int channel, Consumer<Output> consumer) {
		if(this.rootContainer.isClient()) {
			return;
		}

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
		if(!this.rootContainer.isClient()) {
			return;
		}
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
	 * @see #sendToClients(int, Consumer)
	 */
	@Override
	@ApiStatus.OverrideOnly
	protected void receiveFromServer(int channel, Input input) {
		if(channel == TRANSFORM_CHANGE) {
			this.transformation = GuiUtil.readTransformation(input);
			this.invertedMatrix = null;
		} else if(channel == BOUNDS_CHANGE) {
			this.bounds = GuiUtil.readPolygon(input);
		}
	}

	@Override
	@ApiStatus.OverrideOnly
	protected void receiveFromClient(NetworkMember member, int channel, Input input) {
	}

	public Transformation getTransformation() {
		return this.transformation;
	}

	public Drawable setTransformation(Transformation transformation) {
		this.transformation = transformation;
		this.invertedMatrix = null;
		this.sendToClients(TRANSFORM_CHANGE, output -> GuiUtil.write(transformation, output));
		return this;
	}

	/**
	 * @return the untransformed bounds of this drawable
	 */
	public Polygon getBounds() {
		return this.bounds;
	}

	/**
	 * a second setBounds method that is protected, which allows you to throw UnsupportedOperationExceptions in {@link #setBounds(Polygon)} while
	 * still being able to change the bounds yourself
	 */
	protected void setBoundsProtected(Polygon polygon) {
		this.bounds = polygon;
		this.sendToClients(BOUNDS_CHANGE, output -> GuiUtil.write(polygon, output));
	}

	protected void setTransformationProtected(Transformation transformation) {
		this.transformation = transformation;
		this.invertedMatrix = null;
		this.sendToClients(TRANSFORM_CHANGE, output -> GuiUtil.write(transformation, output));
	}

	public void setBounds(Polygon polygon) {
		this.bounds = polygon;
		this.sendToClients(BOUNDS_CHANGE, output -> GuiUtil.write(polygon, output));
	}

	/**
	 * @return get the matrix used to invert points so they make sense from the Drawable's perspective
	 */
	public Matrix4f getInvertedMatrix() {
		Matrix4f invertedMatrix = this.invertedMatrix;
		if(invertedMatrix == null) {
			Transformation transformation = this.transformation;
			Matrix4f m4f = transformation.getModelMatrixTransform().copy();
			if(m4f.invert()) {
				this.invertedMatrix = m4f;
			} else {
				this.invertedMatrix = transformation.getModelMatrixTransform();
			}
			invertedMatrix = m4f;
		}
		return invertedMatrix;
	}
}
