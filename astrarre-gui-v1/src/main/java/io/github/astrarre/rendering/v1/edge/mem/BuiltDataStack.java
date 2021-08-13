package io.github.astrarre.rendering.v1.edge.mem;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * a stack of memory, this is used instead of objects for performance
 */
public final class BuiltDataStack {
	public static final BuiltDataStack EMPTY = new BuiltDataStack(Unpooled.buffer(0, 0), List.of());

	final ByteBuf primitives;
	final List<Object> objects;
	int objectsIndex;
	
	public BuiltDataStack(ByteBuf primitives, List<Object> objects) {
		this.primitives = primitives;
		this.objects = objects;
	}

	public void reset() {
		this.primitives.readerIndex(0);
		this.objectsIndex = 0;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T pop() {
		return (T) this.objects.get(this.objectsIndex++);
	}

	public boolean popBoolean() {
		return this.primitives.readBoolean();
	}

	public byte popByte() {
		return this.primitives.readByte();
	}

	public short popShort() {
		return this.primitives.readShort();
	}

	public int popInt() {
		return this.primitives.readInt();
	}

	public long popLong() {
		return this.primitives.readLong();
	}

	public char popChar() {
		return this.primitives.readChar();
	}

	public float popFloat() {
		return this.primitives.readFloat();
	}

	public double popDouble() {
		return this.primitives.readDouble();
	}

	public byte[] popBytes(int length) {
		byte[] arr = new byte[length];
		this.primitives.readBytes(arr);
		return arr;
	}

	public ByteBuf popBytesBuf(int length) {
		return this.primitives.readBytes(length);
	}

	public void popBytes(ByteBuf dst) {
		this.primitives.readBytes(dst);
	}

	public void popBytes(ByteBuf dst, int length) {
		this.primitives.readBytes(dst, length);
	}

	public void popBytes(ByteBuf dst, int dstIndex, int length) {
		this.primitives.readBytes(dst, dstIndex, length);
	}

	public void popBytes(byte[] dst) {
		this.primitives.readBytes(dst);
	}

	public void popBytes(byte[] dst, int dstIndex, int length) {
		this.primitives.readBytes(dst, dstIndex, length);
	}

	public void popBytes(ByteBuffer dst) {
		this.primitives.readBytes(dst);
	}

	public void popBytes(OutputStream out, int length) throws IOException {
		this.primitives.readBytes(out, length);
	}

	public int popBytes(GatheringByteChannel out, int length) throws IOException {
		return this.primitives.readBytes(out, length);
	}

	public CharSequence popCharSequence(int length, Charset charset) {
		return this.primitives.readCharSequence(length, charset);
	}

	public int popBytes(FileChannel out, long position, int length) throws IOException {
		return this.primitives.readBytes(out, position, length);
	}
}
