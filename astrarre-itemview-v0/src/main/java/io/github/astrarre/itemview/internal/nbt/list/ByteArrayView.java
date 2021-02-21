package io.github.astrarre.itemview.internal.nbt.list;

import java.util.Arrays;

import io.github.astrarre.itemview.v0.fabric.FabricViews;
import it.unimi.dsi.fastutil.bytes.AbstractByteList;

import net.minecraft.nbt.ByteArrayTag;

public final class ByteArrayView extends AbstractByteList {
	private final ByteArrayTag array;

	/**
	 * @deprecated unsafe
	 * @see #createCopy(byte[])
	 */
	@Deprecated
	public static ByteArrayView create(byte[] array) {
		return new ByteArrayView(new ByteArrayTag(array));
	}

	public static ByteArrayView createCopy(byte[] array) {
		return create(Arrays.copyOf(array, array.length));
	}

	private ByteArrayView(ByteArrayTag tag) {this.array = tag;}

	@Override
	public byte getByte(int index) {
		return this.array.getByteArray()[index];
	}

	@Override
	public int size() {
		return this.array.size();
	}

	// internal
	/**
	 * @deprecated internal
	 * @see FabricViews#view(ByteArrayTag)
	 */
	@Deprecated
	public static ByteArrayView create(ByteArrayTag tag) {
		return new ByteArrayView(tag);
	}

	// for fabric mods, not for astrarre mods!

	/**
	 * @deprecated unsafe
	 * @see #asCopiedTag()
	 */
	@Deprecated
	public ByteArrayTag asTag() {
		return this.array;
	}

	public ByteArrayTag asCopiedTag() {
		return (ByteArrayTag) this.array.copy();
	}
}
