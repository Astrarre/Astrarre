package io.github.astrarre.itemview.internal.nbt.list;

import java.util.Arrays;
import net.minecraft.nbt.NbtLongArray;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import it.unimi.dsi.fastutil.longs.AbstractLongList;

public final class LongArrayView extends AbstractLongList {
	private final NbtLongArray array;

	/**
	 * @deprecated unsafe
	 * @see #createCopy(long[])
	 */
	@Deprecated
	public static LongArrayView create(long[] array) {
		return new LongArrayView(new NbtLongArray(array));
	}

	public static LongArrayView createCopy(long[] array) {
		return create(Arrays.copyOf(array, array.length));
	}

	private LongArrayView(NbtLongArray tag) {this.array = tag;}

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
	 * @see FabricViews#view(NbtLongArray)
	 */
	@Deprecated
	public static LongArrayView create(NbtLongArray tag) {
		return new LongArrayView(tag);
	}

	// for fabric mods, not for astrarre mods!

	/**
	 * @deprecated unsafe
	 * @see #asCopiedTag()
	 */
	@Deprecated
	public NbtLongArray asTag() {
		return this.array;
	}

	public NbtLongArray asCopiedTag() {
		return this.array.copy();
	}
}
