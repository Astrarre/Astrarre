package io.github.astrarre.hash.v0.api;

import java.io.OutputStream;
import java.nio.charset.Charset;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import io.github.astrarre.hash.impl.IdentifierPacker;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface Hasher extends PrimitiveSink {
	<T> Hasher putObject(T instance, Funnel<? super T> funnel);

	/**
	 * includes the stack count in hash
	 */
	Hasher putItemStack(ItemStack stack);

	/**
	 * puts an itemstack + null tag
	 */
	Hasher putItemExact(Item item);

	Hasher putItemStackIgnoreCount(ItemStack stack);

	/**
	 * does not include stack count in hash
	 */
	Hasher putItemKey(ItemKey key);

	Hasher putNbt(NbtElement element);

	Hasher putNbt(NbtValue value);

	<T> Hasher putRegistry(T object, Registry<T> registry);

	Hasher putIdentifier(Identifier id);

	Hasher putIdentifier(String namespace, String path);

	Hasher putIdentifier(Id id);

	Hasher putBlockState(BlockState state);

	/**
	 * It doesn't have to be packable, just likely enough
	 * @see IdentifierPacker
	 */
	default Hasher putPackableString(String pack, Charset charset) {
		long packed = IdentifierPacker.pack(pack);
		if(packed == -1) {
			return this.putString(pack, charset);
		} else {
			return this.putLong(packed);
		}
	}

	<T extends Comparable<T>> Hasher putProperty(BlockState state, Property<T> property);

	@Override
	Hasher putByte(byte b);

	@Override
	Hasher putBytes(byte[] bytes);

	@Override
	Hasher putBytes(byte[] bytes, int off, int len);

	@Override
	Hasher putShort(short s);

	@Override
	Hasher putInt(int i);

	@Override
	Hasher putLong(long l);

	@Override
	Hasher putFloat(float f);

	@Override
	Hasher putDouble(double d);

	@Override
	Hasher putBoolean(boolean b);

	@Override
	Hasher putChar(char c);

	@Override
	Hasher putUnencodedChars(CharSequence charSequence);

	@Override
	Hasher putString(CharSequence charSequence, Charset charset);

	long getVersion();

	OutputStream asOutputStream();
}
