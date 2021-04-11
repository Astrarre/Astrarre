package io.github.astrarre.gui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import io.github.astrarre.gui.internal.properties.ClientSyncedProperty;
import io.github.astrarre.gui.internal.properties.DefaultProperty;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

public class DrawableSerializer implements Serializer<ADrawable> {
	protected final RootContainer container;

	public DrawableSerializer(RootContainer container) {
		this.container = container;
	}

	@Override
	public ADrawable read(NbtValue value) {
		NBTagView input = value.asTag();
		Id id = Serializer.ID.read(input, "registryId");
		Function<NBTagView, ADrawable> function = DrawableRegistry.forId(id);
		if (function == null) {
			throw new IllegalStateException("No serialized for id: " + id);
		} else {
			int syncId = input.getInt("syncId");
			DrawableInternal.IS_CLIENT.set(true);
			ADrawable drawable = function.apply(input.getTag("serialized", NBTagView.EMPTY));
			DrawableInternal.IS_CLIENT.set(false);
			((DrawableInternal) drawable).id = syncId;

			for (NBTagView tag : input.get("property", NBTType.listOf(NBTType.TAG), Collections.emptyList())) {
				SyncedProperty<?> property = drawable.forId(tag.getInt("propertyId"));
				if (property instanceof DefaultProperty) {
					property.sync(tag.getValue("value"));
				}
			}

			drawable.setBounds(Polygon.SERIALIZER.read(input, "bounds"));
			drawable.setTransformation(Transformation.SERIALIZER.read(input, "transformation"));
			return drawable;
		}
	}

	@Override
	public NbtValue save(ADrawable instance) {
		if (instance.registryId == null) {
			throw new IllegalStateException("Tried to serialize client-only component!");
		}

		NBTagView.Builder output = NBTagView.builder();
		output.put("registryId", Serializer.ID, instance.registryId.id);
		output.putInt("syncId", instance.getSyncId());
		NBTagView.Builder serialized = NBTagView.builder();
		((DrawableInternal) instance).write0(this.container, serialized);
		if (!serialized.isEmpty()) {
			output.putTag("serialized", serialized);
		}
		List<NBTagView> list = new ArrayList<>();
		for (SyncedProperty value : instance.getProperties()) {
			if (value instanceof ClientSyncedProperty) {
				NBTagView.Builder tag = NBTagView.builder();
				tag.putInt("propertyId", ((ClientSyncedProperty<?>) value).id);
				tag.put("value", value.serializer, value.get());
				list.add(tag);
			}
		}
		output.put("property", NBTType.listOf(NBTType.TAG), list);
		output.put("bounds", Polygon.SERIALIZER, instance.getBounds());
		output.put("transformation", Transformation.SERIALIZER, instance.getTransformation());
		return output;
	}
}
