package io.github.astrarre.itemview.internal.nbt.list;

import java.util.Arrays;
import net.minecraft.nbt.NbtByteArray;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import it.unimi.dsi.fastutil.bytes.AbstractByteList;

public final class ByteArrayView extends AbstractByteList {
	private final NbtByteArray array;

	/**
	 * @deprecated unsafe
	 * @see #createCopy(byte[])
	 */
	@Deprecated
	public static ByteArrayView create(byte[] array) {
		return new ByteArrayView(new NbtByteArray(array));
	}

	public static ByteArrayView createCopy(byte[] array) {
		return create(Arrays.copyOf(array, array.length));
	}

	private ByteArrayView(NbtByteArray tag) {this.array = tag;}

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
	 * @see FabricViews#view(NbtByteArray)
	 */
	@Deprecated
	public static ByteArrayView create(NbtByteArray tag) {
		return new ByteArrayView(tag);
	}

	// for fabric mods, not for astrarre mods!

	/**
	 * @deprecated unsafe
	 * @see #asCopiedTag()
	 */
	@Deprecated
	public NbtByteArray asTag() {
		return this.array;
	}

	public NbtByteArray asCopiedTag() {
		return (NbtByteArray) this.array.copy();
	}
}
