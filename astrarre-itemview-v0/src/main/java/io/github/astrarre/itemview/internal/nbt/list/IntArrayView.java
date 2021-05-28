package io.github.astrarre.itemview.internal.nbt.list;

import java.util.Arrays;
import net.minecraft.nbt.NbtIntArray;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import it.unimi.dsi.fastutil.ints.AbstractIntList;

public final class IntArrayView extends AbstractIntList {
	private final NbtIntArray array;

	/**
	 * @deprecated unsafe
	 * @see #createCopy(int[])
	 */
	@Deprecated
	public static IntArrayView create(int[] array) {
		return new IntArrayView(new NbtIntArray(array));
	}

	public static IntArrayView createCopy(int[] array) {
		return create(Arrays.copyOf(array, array.length));
	}

	private IntArrayView(NbtIntArray tag) {this.array = tag;}

	@Override
	public int getInt(int index) {
		return this.array.getIntArray()[index];
	}

	@Override
	public int size() {
		return this.array.size();
	}


	// internal

	/**
	 * @deprecated internal
	 * @see FabricViews#view(NbtIntArray)
	 */
	@Deprecated
	public static IntArrayView create(NbtIntArray tag) {
		return new IntArrayView(tag);
	}

	// for fabric mods, not for astrarre mods!

	/**
	 * @deprecated unsafe
	 * @see #asCopiedTag()
	 */
	@Deprecated
	public NbtIntArray asTag() {
		return this.array;
	}

	public NbtIntArray asCopiedTag() {
		return this.array.copy();
	}
}
