package io.github.astrarre.hash.v0.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import io.github.astrarre.hash.impl.SmallBuf;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteLists;
import org.jetbrains.annotations.NotNull;

public abstract class FixedBuffer<Self extends FixedBuffer<Self>> implements Comparable<Self> {
	public abstract byte getByte(int index);

	public abstract int bytes();

	public abstract void write(byte[] buf, int off);

	public void append(StringBuilder builder) {
		builder.ensureCapacity(builder.capacity() + 128);
		for(int i = 0; i < this.bytes(); i++) {
			byte b = this.getByte(i);
			builder.append(SmallBuf.HEX_ARRAY_C[(b & 0xF0) >>> 4]);
			builder.append(SmallBuf.HEX_ARRAY_C[b & 0x0F]);
		}
	}

	public NbtValue write() {
		return NbtValue.of(NBTType.BYTE_ARRAY, ByteArrayList.wrap(this.toByteArray()));
	}

	public String toString64() {
		return Base64.getMimeEncoder().encodeToString(this.toByteArray());
	}

	public void write(OutputStream stream) throws IOException {
		byte[] data = SmallBuf.INSTANCE.buffer;
		int off = SmallBuf.INSTANCE.getSection();
		this.write(data, off);
		stream.write(data, off, this.bytes());
	}

	public byte[] toByteArray() {
		byte[] arr = new byte[this.bytes()];
		this.write(arr, 0);
		return arr;
	}

	@Override
	public int compareTo(@NotNull Self o) {
		for(int i = 0; i < this.bytes(); i++) {
			int comp = this.getByte(i) - o.getByte(i);
			if(comp != 0) {
				return comp;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		this.append(builder);
		return builder.toString();
	}
}
