package io.github.astrarre.gui.v0.rei;

import java.util.Collection;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.fabric.adapter.AElementAdapter;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.widgets.Slot;
import me.shedaniel.rei.api.widgets.Widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class REIDrawableHelper {
	public static ADrawable of(Collection<EntryStack> stackList) {
		return new ASlotAdapter(stackList);
	}

	@Environment(EnvType.CLIENT)
	protected static final class ASlotAdapter extends AElementAdapter<Slot> {
		public ASlotAdapter(Collection<EntryStack> stackList) {
			super(null);
			this.drawable = Widgets.createSlot(new Point(0, 0));
			this.drawable.entries(stackList);
		}

		@Override
		public void tick(RootContainer container) {

		}

		@Override
		protected void write0(RootContainer container, NBTagView.Builder output) {
			// client-only
		}
	}
}