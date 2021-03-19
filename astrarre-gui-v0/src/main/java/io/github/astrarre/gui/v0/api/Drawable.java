package io.github.astrarre.gui.v0.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.gui.internal.DrawableInternal;
import io.github.astrarre.gui.internal.GuiPacketHandler;
import io.github.astrarre.gui.internal.GuiUtil;
import io.github.astrarre.gui.internal.properties.ClientSyncedProperty;
import io.github.astrarre.gui.internal.properties.DefaultProperty;
import io.github.astrarre.gui.internal.properties.ServerSyncedProperty;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.gui.v0.api.event.DrawableChange;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Matrix4f;

public abstract class Drawable extends DrawableInternal {
	public static final int BOUNDS_CHANGE = -1;
	public static final int TRANSFORM_CHANGE = -2;
	public static final int PROPERTY_SYNC = -3;

	/**
	 * null if client only component
	 * @see DrawableRegistry#register(Id, Function)
	 */
	@Nullable
	public final DrawableRegistry.Entry registryId;

	private final List<SyncedProperty<?>> properties = new ArrayList<>();
	private final List<DrawableChange.BoundsListener> boundsListeners = new ArrayList<>();
	private final List<DrawableChange.TransformationListener> transformListeners = new ArrayList<>();
	Transformation transformation = Transformation.EMPTY;
	Matrix4f invertedMatrix;
	Polygon bounds = Polygon.EMPTY;

	public Drawable(DrawableRegistry.@Nullable Entry id) {
		this.registryId = id;
	}

	public final void render(RootContainer container, Graphics3d graphics, float tickDelta) {
		try (Close close = graphics.applyTransformation(this.getTransformation())) {
			this.render0(container, graphics, tickDelta);
		}
	}

	protected abstract void render0(RootContainer container, Graphics3d graphics, float tickDelta);

	/**
	 * properties are automatically serialized/deserialized. The order in which these are initialized must be consistent between server and client
	 *
	 * @return a new property who's values are synced to the server
	 */
	protected <T> SyncedProperty<T> createServerSyncedProperty(ToPacketSerializer<T> serializer, T defaultValue) {
		SyncedProperty<T> property;
		if (this.isClient()) {
			property = new ServerSyncedProperty<>(serializer, this, this.properties.size());
			property.setRaw(defaultValue);
		} else {
			property = new DefaultProperty<>(serializer);
		}

		this.properties.add(property);
		return property;
	}

	/**
	 * properties are automatically serialized/deserialized. The order in which these are initialized must be consistent between server and client
	 *
	 * @return a new property who's values are synced to the clients
	 */
	protected <T> SyncedProperty<T> createClientSyncedProperty(ToPacketSerializer<T> serializer, T defaultValue) {
		SyncedProperty<T> property;
		if (!this.isClient()) {
			property = new ClientSyncedProperty<>(serializer, this, this.properties.size());
			property.setRaw(defaultValue);
		} else {
			property = new DefaultProperty<>(serializer);
		}

		this.properties.add(property);
		return property;
	}

	/**
	 * called when the drawable is removed
	 */
	public void remove(RootContainer container) {
	}

	/**
	 * @throws UnsupportedOperationException if this is a clientside component only and cannot be sent from the server
	 */
	public final void write(RootContainer container, Output output) {
		if(this.registryId == null) {
			throw new IllegalStateException("Tried to serialize client-only component!");
		}

		output.writeId(this.registryId.id);
		output.writeInt(this.getSyncId());
		this.write0(container, output);
		int count = (int) this.properties.stream().filter(ClientSyncedProperty.class::isInstance).count();
		output.writeInt(count);
		for (SyncedProperty value : this.properties) {
			if (value instanceof ClientSyncedProperty) {
				output.writeInt(((ClientSyncedProperty<?>) value).id);
				value.serializer.write(output, value.get());
			}
		}

		GuiUtil.write(this.getBounds(), output);
		GuiUtil.write(this.getTransformation(), output);
	}

	public static Drawable read(Input input) {
		Id id = input.readId();
		Function<Input, Drawable> function = DrawableRegistry.forId(id);
		if (function == null || input.bytes() < 4) {
			throw new IllegalStateException("Broken (d/s)erializer! " + id);
		} else {
			int syncId = input.readInt();
			IS_CLIENT.set(true);
			Drawable drawable = function.apply(input);
			IS_CLIENT.set(false);
			GuiUtil.setSyncId(drawable, syncId);
			int count = input.readInt();
			for (int i = 0; i < count; i++) {
				int propertyId = input.readInt();
				SyncedProperty<?> property = drawable.properties.get(propertyId);
				if (property instanceof DefaultProperty) {
					property.onSync(property.serializer.read(input));
				}
			}
			drawable.bounds = GuiUtil.readPolygon(input);
			drawable.transformation = GuiUtil.readTransformation(input);
			drawable.invertedMatrix = null;
			return drawable;
		}
	}

	protected abstract void write0(RootContainer container, Output output);

	/**
	 * send a packet to the server side counterpart of this drawable
	 *
	 * @param channel an internal channel id for this specific drawable
	 * @see DrawableInternal#receiveFromServer(RootContainer, int, Input)
	 */
	public final void sendToServer(int channel, Consumer<Output> consumer) {
		if (!this.isClient()) {
			return;
		}

		for (RootContainer root : this.roots) {
			ModPacketHandler.INSTANCE.sendToServer(GuiPacketHandler.DRAWABLE_PACKET_CHANNEL, output -> {
				output.writeInt(channel);
				output.writeEnum(root.getType());
				output.writeInt(this.getSyncId());
				consumer.accept(output);
			});
		}
	}

	/**
	 * called when a drawable-specific packet is recieved from the server.
	 *
	 * @see #sendToClients(int, Consumer)
	 */
	@Override
	@ApiStatus.OverrideOnly
	protected void receiveFromServer(RootContainer container, int channel, Input input) {
		if (channel == TRANSFORM_CHANGE) {
			Transformation old = this.transformation;
			Transformation current = this.transformation = GuiUtil.readTransformation(input);
			if(!old.equals(current)) {
				this.invertedMatrix = null;
				for (DrawableChange.TransformationListener listener : this.transformListeners) {
					listener.onTransformationChange(this, old, current);
				}
			}
		} else if (channel == BOUNDS_CHANGE) {
			Polygon old = this.bounds;
			Polygon current = this.bounds = GuiUtil.readPolygon(input);
			if(!old.equals(current)) {
				for (DrawableChange.BoundsListener listener : this.boundsListeners) {
					listener.onBoundsChange(this, old, current);
				}
			}
		} else if (channel == PROPERTY_SYNC) {
			int id = input.readInt();
			SyncedProperty<?> property = this.properties.get(id);
			if (property instanceof DefaultProperty) {
				property.onSync(property.serializer.read(input));
			}
		}
	}

	@Override
	@ApiStatus.OverrideOnly
	protected void receiveFromClient(RootContainer container, NetworkMember member, int channel, Input input) {
		if (channel == PROPERTY_SYNC) {
			int id = input.readInt();
			SyncedProperty<?> property = this.properties.get(id);
			if (property instanceof DefaultProperty) {
				property.onSync(property.serializer.read(input));
			}
		}
	}

	public Transformation getTransformation() {
		return this.transformation;
	}

	public Drawable setTransformation(Transformation transformation) {
		Transformation old = this.transformation;
		this.transformation = transformation;
		this.invertedMatrix = null;
		for (DrawableChange.TransformationListener listener : this.transformListeners) {
			listener.onTransformationChange(this, old, transformation);
		}
		this.sendToClients(TRANSFORM_CHANGE, output -> GuiUtil.write(transformation, output));
		return this;
	}

	@Override
	@ApiStatus.OverrideOnly
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
	}

	/**
	 * send a packet to the client side counterparts of this drawable
	 *
	 * @param channel an internal channel id for this specific drawable
	 */
	public final void sendToClients(int channel, Consumer<Output> consumer) {
		if (this.isClient()) {
			return;
		}

		for (RootContainer root : this.roots) {
			NetworkMember member = root.getViewer();
			if(member != null)
			member.send(GuiPacketHandler.DRAWABLE_PACKET_CHANNEL, output -> {
				output.writeInt(channel);
				output.writeEnum(root.getType());
				output.writeInt(this.getSyncId());
				consumer.accept(output);
			});
		}
	}

	/**
	 * @return the untransformed bounds of this drawable
	 */
	public Polygon getBounds() {
		return this.bounds;
	}

	/**
	 * it is expected that the bounds of the polygon has it's origin at [0, 0]. For example, a square's top left corner should be [0, 0]
	 */
	public void setBounds(Polygon polygon) {
		this.validateBounds(polygon);
		Polygon old = this.bounds;
		this.bounds = polygon;
		for (DrawableChange.BoundsListener listener : this.boundsListeners) {
			listener.onBoundsChange(this, old, polygon);
		}
		this.sendToClients(BOUNDS_CHANGE, output -> GuiUtil.write(polygon, output));
	}

	/**
	 * a second setBounds method that is protected, which allows you to throw UnsupportedOperationExceptions in {@link #setBounds(Polygon)} while
	 * still being able to change the bounds yourself
	 */
	protected void setBoundsProtected(Polygon polygon) {
		this.validateBounds(polygon);
		Polygon old = this.bounds;
		this.bounds = polygon;
		for (DrawableChange.BoundsListener listener : this.boundsListeners) {
			listener.onBoundsChange(this, old, polygon);
		}
		this.sendToClients(BOUNDS_CHANGE, output -> GuiUtil.write(polygon, output));
	}

	protected void setTransformationProtected(Transformation transformation) {
		Transformation old = this.transformation;
		this.transformation = transformation;
		this.invertedMatrix = null;
		for (DrawableChange.TransformationListener listener : this.transformListeners) {
			listener.onTransformationChange(this, old, transformation);
		}
		this.sendToClients(TRANSFORM_CHANGE, output -> GuiUtil.write(transformation, output));
	}

	/**
	 * @return get the matrix used to invert points so they make sense from the Drawable's perspective
	 */
	public Matrix4f getInvertedMatrix() {
		Matrix4f invertedMatrix = this.invertedMatrix;
		if (invertedMatrix == null) {
			Transformation transformation = this.getTransformation();
			Matrix4f m4f = transformation.getModelMatrixTransform().copy();
			if (m4f.invert()) {
				this.invertedMatrix = m4f;
			} else {
				this.invertedMatrix = transformation.getModelMatrixTransform();
			}
			invertedMatrix = m4f;
		}
		return invertedMatrix;
	}

	protected void validateBounds(Polygon polygon) {
		if(Validate.IS_DEV) {
			float minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
			for (int i = 0; i < polygon.vertices(); i++) {
				float x = polygon.getX(i), y = polygon.getY(i);
				if(x < minX) {
					minX = x;
				}
				if(y < minY) {
					minY = y;
				}
			}
			if(Math.abs(minX) > .01f || Math.abs(minY) > .01f) {
				throw new IllegalArgumentException(minX + " < 0 | " + minY + " < 0");
			}
		}
	}

	// listeners are not synced to the server!

	public void addTransformationChangeListener(DrawableChange.TransformationListener listener) {
		this.transformListeners.add(listener);
	}

	public void addBoundsChangeListener(DrawableChange.BoundsListener listener) {
		this.boundsListeners.add(listener);
	}

	public void removeListener(DrawableChange.TransformationListener drawables) {
		this.transformListeners.remove(drawables);
	}

	public void removeListener(DrawableChange.BoundsListener drawables) {
		this.boundsListeners.remove(drawables);
	}
}
