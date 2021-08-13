package io.github.astrarre.rendering.v1.edge.mem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSettingInternal;
import io.github.astrarre.util.v0.api.Validate;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class DataStack {
	ByteBuf primitives;
	List<Object> objects;
	ShaderSetting<?> next;

	public DataStack push(Object obj) {
		List<Object> buf = this.objects;
		if(buf == null) {
			this.objects = buf = new ArrayList<>();
		}
		buf.add(obj);
		return this;
	}

	public DataStack pushBytes(byte[] buf, int off, int len) {
		this.primitives().writeBytes(buf, off, len);
		return this;
	}

	public DataStack pushBytes(byte[] buf) {
		this.primitives().writeBytes(buf);
		return this;
	}

	public DataStack pushBytes(ByteBuf src) {
		this.primitives().writeBytes(src);
		return this;
	}

	public DataStack pushBytes(ByteBuf src, int length) {
		this.primitives().writeBytes(src, length);
		return this;
	}

	public DataStack pushBytes(ByteBuf src, int srcIndex, int length) {
		this.primitives().writeBytes(src, srcIndex, length);
		return this;
	}

	public DataStack pushBytes(ByteBuffer src) {
		this.primitives().writeBytes(src);
		return this;
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

	public DataStack pushByte(byte b) {
		this.primitives().writeByte(b);
		return this;
	}

	public DataStack pushBool(boolean b) {
		this.primitives().writeBoolean(b);
		return this;
	}

	public DataStack pushChar(char c) {
		this.primitives().writeChar(c);
		return this;
	}

	public DataStack pushShort(short s) {
		this.primitives().writeShort(s);
		return this;
	}

	public DataStack pushInt(int i) {
		this.primitives().writeInt(i);
		return this;
	}

	public DataStack pushFloat(float f) {
		this.primitives().writeFloat(f);
		return this;
	}

	public DataStack pushLong(long l) {
		this.primitives().writeLong(l);
		return this;
	}

	public DataStack pushDouble(double d) {
		this.primitives().writeDouble(d);
		return this;
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

	public boolean isReset() {
		return this.primitives == null && this.objects == null;
	}

	public final void setSetting(ShaderSetting setting) {
		ShaderSetting expected = this.next;
		Validate.isTrue(expected == null || expected == setting, "ShaderSettings were not fully configured in the correct order!");
		Global global = ShaderSettingInternal.next(setting);
		if(global instanceof ShaderSetting s) {
			this.next = s;
		} else {
			this.next = null;
		}
	}

	final ByteBuf primitives() {
		ByteBuf buf = this.primitives;
		if(buf == null) {
			this.primitives = buf = Unpooled.buffer();
		}
		return buf;
	}
}
