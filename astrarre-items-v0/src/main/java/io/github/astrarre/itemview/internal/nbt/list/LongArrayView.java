package io.github.astrarre.itemview.internal.nbt.list;

import java.util.Arrays;

import io.github.astrarre.itemview.internal.FabricItemViews;
import it.unimi.dsi.fastutil.longs.AbstractLongList;

import net.minecraft.nbt.LongArrayTag;

public final class LongArrayView extends AbstractLongList {
	private final LongArrayTag array;

	/**
	 * @deprecated unsafe
	 * @see #createCopy(long[])
	 */
	@Deprecated
	public static LongArrayView create(long[] array) {
		return new LongArrayView(new LongArrayTag(array));
	}

	public static LongArrayView createCopy(long[] array) {
		return create(Arrays.copyOf(array, array.length));
	}

	private LongArrayView(LongArrayTag tag) {this.array = tag;}

	@Override
	public long getLong(int index) {
		return this.array.getLongArray()[index];
	}

	@Override
	public int size() {
		return this.array.size();
	}


	// internal

	/**
	 * @deprecated internal
	 * @see FabricItemViews#view(LongArrayTag)
	 */
	@Deprecated
	public static LongArrayView create(LongArrayTag tag) {
		return new LongArrayView(tag);
	}

	// for fabric mods, not for astrarre mods!

	/**
	 * @deprecated unsafe
	 * @see #asCopiedTag()
	 */
	@Deprecated
	public LongArrayTag asTag() {
		return this.array;
	}

	public LongArrayTag asCopiedTag() {
		return this.array.copy();
	}
}
