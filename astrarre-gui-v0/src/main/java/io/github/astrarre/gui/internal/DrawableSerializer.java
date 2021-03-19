package io.github.astrarre.gui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import io.github.astrarre.gui.internal.properties.ClientSyncedProperty;
import io.github.astrarre.gui.internal.properties.DefaultProperty;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

public class DrawableSerializer implements Serializer<Drawable> {
	protected final RootContainer container;

	public DrawableSerializer(RootContainer container) {
		this.container = container;
	}

	@Override
	public Drawable read(NBTagView input, String key) {
		Id id = Serializer.ID.read(input, "registryId");
		Function<NBTagView, Drawable> function = DrawableRegistry.forId(id);
		if (function == null) {
			throw new IllegalStateException("No serialized for id: " + id);
		} else {
			int syncId = input.getInt("syncId");
			DrawableInternal.IS_CLIENT.set(true);
			Drawable drawable = function.apply(input.getTag("serialized", NBTagView.EMPTY));
			DrawableInternal.IS_CLIENT.set(false);
			((DrawableInternal) drawable).id = syncId;

			for (NBTagView tag : input.get("properties", NBTType.listOf(NBTType.TAG), Collections.emptyList())) {
				SyncedProperty<?> property = drawable.forId(tag.getInt("propertyId"));
				if (property instanceof DefaultProperty) {
					property.onSync(tag, "value");
				}
			}

			drawable.setBounds(Polygon.SERIALIZER.read(input, "bounds"));
			drawable.setTransformation(Transformation.SERIALIZER.read(input, "transformation"));
			return drawable;
		}
	}

	@Override
	public void save(NBTagView.Builder output, String key, Drawable instance) {
		if (instance.registryId == null) {
			throw new IllegalStateException("Tried to serialize client-only component!");
		}

		Serializer.ID.save(output, "registryId", instance.registryId.id);
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
				value.serializer.save(output, "value", value.get());
			}
		}
		output.put("property", NBTType.listOf(NBTType.TAG), list);
		Polygon.SERIALIZER.save(output, "bounds", instance.getBounds());
		Transformation.SERIALIZER.save(output, "transformation", instance.getTransformation());
	}
}
