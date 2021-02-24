package io.github.astrarre.gui.v0.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.github.astrarre.networking.v0.api.Input;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.Nullable;

/**
 * a registry for all Drawables that are meant to be serialized to the client
 */
public class SyncDrawableRegistry {
	private static final Map<Id, Function<Input, Drawable>> REGISTRY = new HashMap<>();

	public static void register(Id id, Function<Input, Drawable> drawable) {
		Validate.isNull(REGISTRY.put(id, drawable), "Registry entry was overriden!");
	}

	@Nullable
	public static Drawable read(Id id, Input input) {
		Function<Input, Drawable> function = REGISTRY.get(id);
		if (function == null || input.bytes() < 4) {
			return null;
		} else {
			int syncId = input.readInt();
			Drawable drawable = function.apply(input);
			drawable.initId(syncId);
			return drawable;
		}
	}
}
