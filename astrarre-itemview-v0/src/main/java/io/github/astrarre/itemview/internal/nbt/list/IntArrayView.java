package io.github.astrarre.itemview.internal.nbt.list;

import java.util.Arrays;

import io.github.astrarre.itemview.v0.fabric.FabricViews;
import it.unimi.dsi.fastutil.ints.AbstractIntList;

import net.minecraft.nbt.IntArrayTag;

public final class IntArrayView extends AbstractIntList {
	private final IntArrayTag array;

	/**
	 * @deprecated unsafe
	 * @see #createCopy(int[])
	 */
	@Deprecated
	public static IntArrayView create(int[] array) {
		return new IntArrayView(new IntArrayTag(array));
	}

	public static IntArrayView createCopy(int[] array) {
		return create(Arrays.copyOf(array, array.length));
	}

	private IntArrayView(IntArrayTag tag) {this.array = tag;}

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
	 * @see FabricViews#view(IntArrayTag)
	 */
	@Deprecated
	public static IntArrayView create(IntArrayTag tag) {
		return new IntArrayView(tag);
	}

	// for fabric mods, not for astrarre mods!

	/**
	 * @deprecated unsafe
	 * @see #asCopiedTag()
	 */
	@Deprecated
	public IntArrayTag asTag() {
		return this.array;
	}

	public IntArrayTag asCopiedTag() {
		return this.array.copy();
	}
}
