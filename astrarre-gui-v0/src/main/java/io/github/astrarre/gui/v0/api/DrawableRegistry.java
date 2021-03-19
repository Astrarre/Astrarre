package io.github.astrarre.gui.v0.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.astrarre.gui.v0.api.base.borders.ABeveledBorder;
import io.github.astrarre.gui.v0.api.base.borders.ASimpleBorder;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.base.statik.ABeveledRectangle;
import io.github.astrarre.gui.v0.api.base.statik.ADarkenedBackground;
import io.github.astrarre.gui.v0.api.base.widgets.AButton;
import io.github.astrarre.gui.v0.api.base.widgets.AInfo;
import io.github.astrarre.gui.v0.api.base.widgets.ScrollBar;
import io.github.astrarre.gui.v0.api.base.widgets.list.VerticalListWidget;
import io.github.astrarre.gui.v0.api.base.widgets.APasswordTextField;
import io.github.astrarre.gui.v0.api.base.widgets.ATextFieldWidget;
import io.github.astrarre.gui.v0.fabric.adapter.slot.PlayerSlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.WorldInventorySlot;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class DrawableRegistry {
	private static final Map<Id, Function<Input, Drawable>> REGISTRY = new HashMap<>();

	public static final class Entry {
		public final Id id;
		public final Function<Input, Drawable> initializer;
		private Entry(Id id, Function<Input, Drawable> initializer) {
			this.id = id;
			this.initializer = initializer;
		}
	}

	public interface NewDrawable {
		@Environment(EnvType.CLIENT)
		Drawable init(Entry entry, Input input);
	}

	public static Entry registerForward(Id id, NewDrawable drawable) {
		AtomicReference<Entry> reference = new AtomicReference<>();
		Entry entry = register(id, (i) -> drawable.init(reference.get(), i));
		reference.set(entry);
		return entry;
	}

	public static Entry register(Id id, Function<Input, Drawable> drawable) {
		Validate.isNull(REGISTRY.put(id, drawable), "Registry entry was overriden!");
		return new Entry(id, drawable);
	}

	public static Entry registerNoInput(Id id, Supplier<Drawable> drawable) {
		return register(id, (i) -> drawable.get());
	}

	static {
		ABeveledBorder.init();
		ASimpleBorder.init();
		ACenteringPanel.init();
		APanel.init();
		ABeveledRectangle.init();
		ADarkenedBackground.init();
		AButton.init();
		AInfo.init();
		VerticalListWidget.init();
		APasswordTextField.init();
		ATextFieldWidget.init();
		PlayerSlot.init();
		WorldInventorySlot.init();
		ScrollBar.init();
	}

	@Nullable
	public static Function<Input, Drawable> forId(Id id) {
		return REGISTRY.get(id);
	}
}
