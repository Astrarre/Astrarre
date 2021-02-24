package io.github.astrarre.networking.internal;

import java.io.DataInputStream;
import java.io.IOException;

import io.github.astrarre.networking.v0.api.Input;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.PacketByteBuf;

public class ByteBufDataInput implements Input {
	public final PacketByteBuf buf;
	private String lineBuffer;

	public ByteBufDataInput(PacketByteBuf buf) {this.buf = buf;}

	public void reset() {
		this.lineBuffer = null;
	}

	@Override
	public int read(byte[] buffer) {
		int readable = this.buf.readableBytes();
		this.readFully(buffer, 0, readable);
		return readable;
	}

	@Override
	public int bytes() {
		return this.buf.readableBytes();
	}

	@Override
	public void readFully(byte[] b) {
		this.buf.readBytes(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) {
		this.buf.readBytes(b, off, len);
	}

	@Override
	public int skipBytes(int n) {
		int in = this.buf.readerIndex();
		this.buf.skipBytes(n);
		return this.buf.readerIndex() - in;
	}

	@Override
	public boolean readBoolean() {
		return this.buf.readBoolean();
	}

	@Override
	public byte readByte() {
		return this.buf.readByte();
	}

	@Override
	public int readUnsignedByte() {
		return this.buf.readUnsignedByte();
	}

	@Override
	public short readShort() {
		return this.buf.readShort();
	}

	@Override
	public int readUnsignedShort() {
		return this.buf.readUnsignedShort();
	}

	@Override
	public long readUnsignedInt() {
		return this.buf.readUnsignedInt();
	}

	@Override
	public char readChar() {
		return this.buf.readChar();
	}

	@Override
	public int readInt() {
		return this.buf.readInt();
	}

	@Override
	public long readLong() {
		return this.buf.readLong();
	}

	@Override
	public float readFloat() {
		return this.buf.readFloat();
	}

	@Override
	public double readDouble() {
		return this.buf.readDouble();
	}

	@Override
	public String readLine() {
		if (this.lineBuffer == null) {
			this.lineBuffer = this.readUTF();
		}
		int index = this.lineBuffer.indexOf('\n');
		if(index != -1) {
			this.lineBuffer = this.lineBuffer.substring(index + 1);
			return this.lineBuffer.substring(0, index);
		} else {
			String read = this.lineBuffer;
			this.lineBuffer = null;
			return read;
		}
	}

	@NotNull
	@Override
	public String readUTF() {
		try {
			return DataInputStream.readUTF(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
