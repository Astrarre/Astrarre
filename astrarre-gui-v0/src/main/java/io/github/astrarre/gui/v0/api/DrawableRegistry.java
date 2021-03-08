package io.github.astrarre.gui.v0.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.github.astrarre.gui.v0.api.widgets.ButtonWidget;
import io.github.astrarre.gui.v0.api.panel.CenteringPanel;
import io.github.astrarre.gui.v0.api.statik.DarkenedBackground;
import io.github.astrarre.gui.v0.api.delegates.TaintedDrawable;
import io.github.astrarre.gui.v0.api.widgets.TextFieldWidget;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.Nullable;

public class DrawableRegistry {
	private static final Map<Id, BiFunction<RootContainer, Input, Drawable>> REGISTRY = new HashMap<>();

	public static final class Entry {
		public final Id id;
		public final BiFunction<RootContainer, Input, Drawable> initializer;
		private Entry(Id id, BiFunction<RootContainer, Input, Drawable> initializer) {
			this.id = id;
			this.initializer = initializer;
		}
	}

	public interface NewDrawable {
		Drawable init(RootContainer container, Entry entry, Input input);
	}

	public static Entry registerForward(Id id, NewDrawable drawable) {
		AtomicReference<Entry> reference = new AtomicReference<>();
		Entry entry = register(id, (r, i) -> drawable.init(r, reference.get(), i));
		reference.set(entry);
		return entry;
	}

	public static Entry register(Id id, BiFunction<RootContainer, Input, Drawable> drawable) {
		Validate.isNull(REGISTRY.put(id, drawable), "Registry entry was overriden!");
		return new Entry(id, drawable);
	}

	public static Entry registerNoInput(Id id, Function<RootContainer, Drawable> drawable) {
		return register(id, (r, i) -> drawable.apply(r));
	}

	static {
		ButtonWidget.init();
		Panel.init();
		TextFieldWidget.init();
		CenteringPanel.init();
		DarkenedBackground.init();
		TaintedDrawable.init();
	}

	@Nullable
	public static BiFunction<RootContainer, Input, Drawable> forId(Id id) {
		return REGISTRY.get(id);
	}
}
