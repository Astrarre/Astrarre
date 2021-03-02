package io.github.astrarre.gui.v0.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import io.github.astrarre.gui.v0.api.drawable.Button;
import io.github.astrarre.gui.v0.api.drawable.Slot;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.Nullable;

public class DrawableRegistry {
	private static final Map<Id, BiFunction<RootContainer, Input, Drawable>> REGISTRY = new HashMap<>();

	public static final Entry BUTTON = register(Id.newInstance("astrarre-gui-v0", "button"), Button::new);
	public static final Entry PANEL = register(Id.newInstance("astrarre-gui-v0", "panel"), Panel::new);
	public static final Entry SLOT = register(Id.newInstance("astrarre-gui-v0", "slot"), Slot::new);

	public static final class Entry {
		public final Id id;
		public final BiFunction<RootContainer, Input, Drawable> initializer;
		private Entry(Id id, BiFunction<RootContainer, Input, Drawable> initializer) {
			this.id = id;
			this.initializer = initializer;
		}
	}


	public static Entry register(Id id, BiFunction<RootContainer, Input, Drawable> drawable) {
		Validate.isNull(REGISTRY.put(id, drawable), "Registry entry was overriden!");
		return new Entry(id, drawable);
	}

	@Nullable
	public static BiFunction<RootContainer, Input, Drawable> forId(Id id) {
		return REGISTRY.get(id);
	}
}
