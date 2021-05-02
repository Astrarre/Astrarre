package io.github.astrarre.gui.v0.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import io.github.astrarre.gui.internal.DrawableInternal;
import io.github.astrarre.gui.internal.GUIPacketHandler;
import io.github.astrarre.gui.internal.properties.ClientSyncedProperty;
import io.github.astrarre.gui.internal.properties.DefaultProperty;
import io.github.astrarre.gui.internal.properties.ServerSyncedProperty;
import io.github.astrarre.gui.v0.api.event.DrawableChange;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;

public abstract class ADrawable extends DrawableInternal {
	public static final int BOUNDS_CHANGE = -1;
	public static final int TRANSFORM_CHANGE = -2;
	public static final int PROPERTY_SYNC = -3;
	/**
	 * null if client only component
	 *
	 * @see DrawableRegistry#register(Id, Function)
	 */
	@Nullable public final DrawableRegistry.Entry registryId;
	private final List<SyncedProperty<?>> properties = new ArrayList<>();
	private final List<DrawableChange.BoundsListener> boundsListeners = new ArrayList<>();
	private final List<DrawableChange.TransformationListener> transformListeners = new ArrayList<>();
	Transformation transformation = Transformation.EMPTY;
	Matrix4f invertedMatrix;
	Polygon bounds = Polygon.EMPTY;

	public ADrawable(DrawableRegistry.@Nullable Entry id) {
		this.registryId = id;
	}

	/**
	 * render the component
	 */
	protected abstract void render0(RootContainer container, GuiGraphics graphics, float tickDelta);

	/**
	 * serialize the component
	 */
	@Override
	protected abstract void write0(RootContainer container, NBTagView.Builder output);

	/**
	 * called when the drawable is removed
	 */
	@ApiStatus.OverrideOnly
	public void onRemoved(RootContainer container) {
	}

	/**
	 * if any component returns false, the gui is closed
	 */
	public boolean isValid(RootContainer container, PlayerEntity entity) {
		return true;
	}

	/**
	 * called when a drawable-specific packet is recieved from the server.
	 *
	 * @see #sendToClients(int, NBTagView)
	 */
	@Override
	@ApiStatus.OverrideOnly
	protected void receiveFromServer(RootContainer container, int channel, NBTagView input) {
		if (channel == TRANSFORM_CHANGE) {
			Transformation old = this.transformation;
			Transformation current = this.transformation = Transformation.SERIALIZER.read(input, "transformation");
			if (!old.equals(current)) {
				this.invertedMatrix = null;
				for (DrawableChange.TransformationListener listener : this.transformListeners) {
					listener.onTransformationChange(this, old, current);
				}
			}
		} else if (channel == BOUNDS_CHANGE) {
			Polygon old = this.bounds;
			Polygon current = this.bounds = Polygon.SERIALIZER.read(input, "polygon");
			if (!old.equals(current)) {
				for (DrawableChange.BoundsListener listener : this.boundsListeners) {
					listener.onBoundsChange(this, old, current);
				}
			}
		} else if (channel == PROPERTY_SYNC) {
			int id = input.getInt("propertyId");
			SyncedProperty<?> property = this.properties.get(id);
			if (property instanceof DefaultProperty) {
				property.sync(input.getTag("payload").getValue("value"));
			}
		}
	}

	@Override
	@ApiStatus.OverrideOnly
	protected void receiveFromClient(RootContainer container, NetworkMember member, int channel, NBTagView input) {
		if (channel == PROPERTY_SYNC) {
			int id = input.getInt("propertyId");
			SyncedProperty<?> property = this.properties.get(id);
			if (property instanceof DefaultProperty) {
				property.sync(input.getTag("payload").getValue("value"));
			}
		}
	}

	public final void render(RootContainer container, GuiGraphics graphics, float tickDelta) {
		try (Close close = graphics.applyTransformation(this.getTransformation())) {
			this.render0(container, graphics, tickDelta);
		}
	}

	/**
	 * send a packet to the client side counterparts of this drawable
	 *
	 * @param channel an internal channel id for this specific drawable
	 */
	public final void sendToClients(int channel, NBTagView tag) {
		if (this.isClient()) return;
		for (RootContainer root : this.roots) {
			GUIPacketHandler.sendToClients(root, tag, channel, this.getSyncId());
		}
	}

	/**
	 * send a packet to the server side counterpart of this drawable
	 *
	 * @param channel an internal channel id for this specific drawable
	 * @see #receiveFromServer(RootContainer, int, NBTagView)
	 */
	public final void sendToServer(int channel, NBTagView tag) {
		if (!this.isClient()) {
			return;
		}

		for (RootContainer root : this.roots) {
			GUIPacketHandler.sendToServer(root, tag, channel, this.getSyncId());
		}
	}

	/**
	 * properties are automatically serialized/deserialized. The order in which these are initialized must be consistent between server and client
	 *
	 * @return a new property who's values are synced to the server
	 */
	protected final <T> SyncedProperty<T> createServerSyncedProperty(Serializer<T> serializer, T defaultValue) {
		SyncedProperty<T> property;
		if (this.isClient()) {
			property = new ServerSyncedProperty<>(serializer, this, this.properties.size());
		} else {
			property = new DefaultProperty<>(serializer);
		}
		property.setRaw(defaultValue);

		this.properties.add(property);
		return property;
	}

	/**
	 * properties are automatically serialized/deserialized. The order in which these are initialized must be consistent between server and client
	 *
	 * @return a new property who's values are synced to the clients
	 */
	protected final <T> SyncedProperty<T> createClientSyncedProperty(Serializer<T> serializer, T defaultValue) {
		SyncedProperty<T> property;
		if (!this.isClient()) {
			property = new ClientSyncedProperty<>(serializer, this, this.properties.size());
		} else {
			property = new DefaultProperty<>(serializer);
		}
		property.setRaw(defaultValue);

		this.properties.add(property);
		return property;
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
		Polygon old = this.bounds;
		this.bounds = polygon;
		for (DrawableChange.BoundsListener listener : this.boundsListeners) {
			listener.onBoundsChange(this, old, polygon);
		}
		NBTagView.Builder builder = NBTagView.builder();
		builder.putSerializable("polygon", polygon);
		this.sendToClients(BOUNDS_CHANGE, builder);
	}

	@Override
	@ApiStatus.OverrideOnly
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
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

	public Transformation getTransformation() {
		return this.transformation;
	}

	public ADrawable setTransformation(Transformation transformation) {
		Transformation old = this.transformation;
		this.transformation = transformation;
		this.invertedMatrix = null;
		for (DrawableChange.TransformationListener listener : this.transformListeners) {
			listener.onTransformationChange(this, old, transformation);
		}
		NBTagView.Builder tag = NBTagView.builder();
		tag.putSerializable("transformation", transformation);
		this.sendToClients(TRANSFORM_CHANGE, tag);
		return this;
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

	public SyncedProperty<?> forId(int id) {
		return this.properties.get(id);
	}

	public List<SyncedProperty<?>> getProperties() {
		return Collections.unmodifiableList(this.properties);
	}
}
