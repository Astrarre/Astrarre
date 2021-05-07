package io.github.astrarre.gui.v0.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.astrarre.gui.v0.api.base.AWindowDrawable;
import io.github.astrarre.gui.v0.api.base.borders.ABeveledBorder;
import io.github.astrarre.gui.v0.api.base.borders.ASimpleBorder;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.panel.AGridPanel;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.base.statik.ABeveledRectangle;
import io.github.astrarre.gui.v0.api.base.statik.ADarkenedBackground;
import io.github.astrarre.gui.v0.api.base.widgets.AButton;
import io.github.astrarre.gui.v0.api.base.widgets.AInfo;
import io.github.astrarre.gui.v0.api.base.widgets.ALabel;
import io.github.astrarre.gui.v0.api.base.widgets.APasswordTextField;
import io.github.astrarre.gui.v0.api.base.widgets.AProgressBar;
import io.github.astrarre.gui.v0.api.base.widgets.AScrollBar;
import io.github.astrarre.gui.v0.api.base.widgets.ATextFieldWidget;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ABlockEntityInventorySlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.APlayerSlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.AWorldInventorySlot;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class DrawableRegistry {
	private static final Map<Id, Function<NBTagView, ADrawable>> REGISTRY = new HashMap<>();

	public static final class Entry {
		public final Id id;
		public final Function<NBTagView, ADrawable> initializer;
		private Entry(Id id, Function<NBTagView, ADrawable> initializer) {
			this.id = id;
			this.initializer = initializer;
		}
	}

	public interface NewDrawable {
		@Environment(EnvType.CLIENT)
		ADrawable init(Entry entry, NBTagView input);
	}

	public static Entry registerForward(Id id, NewDrawable drawable) {
		AtomicReference<Entry> reference = new AtomicReference<>();
		Entry entry = register(id, (i) -> drawable.init(reference.get(), i));
		reference.set(entry);
		return entry;
	}

	public static Entry register(Id id, Function<NBTagView, ADrawable> drawable) {
		Validate.isNull(REGISTRY.put(id, drawable), "Registry entry was overriden!");
		return new Entry(id, drawable);
	}

	public static Entry registerNoInput(Id id, Supplier<ADrawable> drawable) {
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
		APasswordTextField.init();
		ATextFieldWidget.init();
		APlayerSlot.init();
		AWorldInventorySlot.init();
		AProgressBar.init();
		ALabel.init();
		AGridPanel.init();
		ABlockEntityInventorySlot.init();
		AWindowDrawable.init();
		AScrollBar.init();
	}

	@Nullable
	public static Function<NBTagView, ADrawable> forId(Id id) {
		return REGISTRY.get(id);
	}
}
