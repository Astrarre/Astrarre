package io.github.astrarre.rendering.v1.edge.mem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class DataStack {
	ByteBuf primitives;
	List<Object> objects;

	final ByteBuf primitives() {
		ByteBuf buf = this.primitives;
		if(buf == null) {
			this.primitives = buf = Unpooled.buffer();
		}
		return buf;
	}

	public void push(Object obj) {
		List<Object> buf = this.objects;
		if(buf == null) {
			this.objects = buf = new ArrayList<>();
		}
		buf.add(obj);
	}
	
	public void pushBytes(byte[] buf, int off, int len) {
		this.primitives().writeBytes(buf, off, len);
	}

	public void pushBytes(byte[] buf) {
		this.primitives().writeBytes(buf);
	}

	public void pushBytes(ByteBuf src) {
		this.primitives().writeBytes(src);
	}

	public void pushBytes(ByteBuf src, int length) {
		this.primitives().writeBytes(src, length);
	}

	public void pushBytes(ByteBuf src, int srcIndex, int length) {
		this.primitives().writeBytes(src, srcIndex, length);
	}

	public void pushBytes(ByteBuffer src) {
		this.primitives().writeBytes(src);
	}

	public int pushBytes(InputStream in, int length) throws IOException {
		return this.primitives().writeBytes(in, length);
	}

	public int pushBytes(ScatteringByteChannel in, int length) throws IOException {
		return this.primitives().writeBytes(in, length);
	}

	public int pushBytes(FileChannel in, long position, int length) throws IOException {
		return this.primitives().writeBytes(in, position, length);
	}

	public int pushCharSequence(CharSequence sequence, Charset charset) {
		return this.primitives().writeCharSequence(sequence, charset);
	}

	public void pushByte(byte b) {
		this.primitives().writeByte(b);
	}

	public void pushBool(boolean b) {
		this.primitives().writeBoolean(b);
	}

	public void pushChar(char c) {
		this.primitives().writeChar(c);
	}

	public void pushShort(short s) {
		this.primitives().writeShort(s);
	}

	public void pushInt(int i) {
		this.primitives().writeInt(i);
	}

	public void pushFloat(float f) {
		this.primitives().writeFloat(f);
	}

	public void pushLong(long l) {
		this.primitives().writeLong(l);
	}

	public void pushDouble(double d) {
		this.primitives().writeDouble(d);
	}

	public BuiltDataStack build() {
		ByteBuf prims = this.primitives;
		List<Object> objs = this.objects;
		if(prims.writerIndex() == 0 && objs.isEmpty()) {
			return BuiltDataStack.EMPTY;
		} else {
			this.primitives = null;
			this.objects = null;
			return new BuiltDataStack(prims, objs);
		}
	}
}
