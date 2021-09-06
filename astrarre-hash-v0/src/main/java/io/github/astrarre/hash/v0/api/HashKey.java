package io.github.astrarre.hash.v0.api;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Random;

import com.google.common.hash.HashCode;
import io.github.astrarre.hash.impl.SmallBuf;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.util.v0.api.Validate;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public final class HashKey extends FixedBuffer<HashKey> {
	public static final int BYTES = 32;
	final long a, b, c, d;

	public HashKey(NbtValue value) {
		this(value.asByteList().toByteArray());
	}

	public HashKey(ByteBuf buf) {
		this(buf.readLong(), buf.readLong(), buf.readLong(), buf.readLong());
	}

	public HashKey(long a, long b, long c, long d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public HashKey(Random random) {
		this(random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong());
	}

	public HashKey(CharSequence base16) {
		this(base16, 0);
	}

	public HashKey(CharSequence base16, int off) {
		int index = off;
		this.a = (((long) Character.digit(base16.charAt(index), 16)) << 60) | Long.parseLong(base16, index+1, index += 16, 16);
		this.b = (((long) Character.digit(base16.charAt(index), 16)) << 60) | Long.parseLong(base16, index+1, index += 16, 16);
		this.c = (((long) Character.digit(base16.charAt(index), 16)) << 60) | Long.parseLong(base16, index+1, index += 16, 16);
		this.d = (((long) Character.digit(base16.charAt(index), 16)) << 60) | Long.parseLong(base16, index+1, index + 16, 16);
	}

	public HashKey(MessageDigest digest) {
		try {
			byte[] data = SmallBuf.INSTANCE.buffer;
			int off = SmallBuf.INSTANCE.getSection();
			digest.digest(data, off, 32);
			this.a = SmallBuf.getLong(data, off + 0);
			this.b = SmallBuf.getLong(data, off + 8);
			this.c = SmallBuf.getLong(data, off + 16);
			this.d = SmallBuf.getLong(data, off + 24);
		} catch(DigestException e) {
			throw Validate.rethrow(e);
		}
	}

	public HashKey(HashCode code) {
		byte[] data = SmallBuf.INSTANCE.buffer;
		int off = SmallBuf.INSTANCE.getSection();
		code.writeBytesTo(data, off, 32);
		this.a = SmallBuf.getLong(data, off + 0);
		this.b = SmallBuf.getLong(data, off + 8);
		this.c = SmallBuf.getLong(data, off + 16);
		this.d = SmallBuf.getLong(data, off + 24);
	}

	public HashKey(byte[] data, int off) {
		this.a = SmallBuf.getLong(data, off + 0);
		this.b = SmallBuf.getLong(data, off + 8);
		this.c = SmallBuf.getLong(data, off + 16);
		this.d = SmallBuf.getLong(data, off + 24);
	}

	public HashKey(InputStream stream) throws IOException {
		byte[] data = SmallBuf.INSTANCE.buffer;
		int off = SmallBuf.INSTANCE.getSection();
		Validate.isTrue(stream.read(data, off, BYTES) == BYTES, "EOF on stream!");
		this.a = SmallBuf.getLong(data, off + 0);
		this.b = SmallBuf.getLong(data, off + 8);
		this.c = SmallBuf.getLong(data, off + 16);
		this.d = SmallBuf.getLong(data, off + 24);
	}

	public HashKey(DataInput input) throws IOException {
		this.a = input.readLong();
		this.b = input.readLong();
		this.c = input.readLong();
		this.d = input.readLong();
	}

	public HashKey(ByteBuffer buffer) {
		this.a = buffer.getLong();
		this.b = buffer.getLong();
		this.c = buffer.getLong();
		this.d = buffer.getLong();
	}

	public HashKey(byte[] chars) {
		this(chars, 0);
	}

	public void hash(Hasher hasher) {
		hasher.putLong(this.a);
		hasher.putLong(this.b);
		hasher.putLong(this.c);
		hasher.putLong(this.d);
	}

	@Override
	public void write(byte[] buf, int off) {
		SmallBuf.writeLong(this.a, buf, off);
		SmallBuf.writeLong(this.b, buf, off + 8);
		SmallBuf.writeLong(this.c, buf, off + 16);
		SmallBuf.writeLong(this.d, buf, off + 24);
	}

	public void write(ByteBuffer buffer) {
		buffer.putLong(this.a);
		buffer.putLong(this.b);
		buffer.putLong(this.c);
		buffer.putLong(this.d);
	}

	public void write(ByteBuf buf) {
		buf.writeLong(this.a);
		buf.writeLong(this.b);
		buf.writeLong(this.c);
		buf.writeLong(this.d);
	}

	public long getLong(int index) {
		return switch(index) {
			case 0 -> this.a;
			case 1 -> this.b;
			case 2 -> this.c;
			case 3 -> this.d;
			default -> throw new ArrayIndexOutOfBoundsException(index + " > 4");
		};
	}

	public int longSize() {
		return 4;
	}

	@Override
	public byte getByte(int index) {
		int modIndex = 56 - (index & 7) * 8;
		return (byte) (this.getLong(index >> 3) >> modIndex & 0xff);
	}

	@Override
	public int bytes() {
		return BYTES;
	}

	@Override
	public int compareTo(@NotNull HashKey o) {
		long comp;
		if((comp = this.a - o.a) != 0) {
			return (int) comp;
		}
		if((comp = this.b - o.b) != 0) {
			return (int) comp;
		}
		if((comp = this.c - o.c) != 0) {
			return (int) comp;
		}
		if((comp = this.d - o.d) != 0) {
			return (int) comp;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		int result = Long.hashCode(this.a);
		result = 31 * result + Long.hashCode(this.b);
		result = 31 * result + Long.hashCode(this.c);
		result = 31 * result + Long.hashCode(this.d);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof HashKey h && this.compareTo(h) == 0;
	}
}
