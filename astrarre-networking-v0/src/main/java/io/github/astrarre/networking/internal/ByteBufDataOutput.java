package io.github.astrarre.networking.internal;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;

import io.github.astrarre.networking.v0.api.Output;
import io.github.astrarre.util.v0.api.Validate;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.PacketByteBuf;

public class ByteBufDataOutput implements Output {
	public final PacketByteBuf buf;

	public ByteBufDataOutput(PacketByteBuf buf) {this.buf = buf;}

	@Override
	public void write(int b) {
		this.buf.writeByte(b);
	}

	@Override
	public void write(byte[] b) {
		this.buf.writeBytes(b);
	}

	@Override
	public void write(byte[] b, int off, int len) {
		this.buf.writeBytes(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) {
		this.buf.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) {
		this.buf.writeByte(v);
	}

	@Override
	public void writeShort(int v) {
		this.buf.writeShort(v);
	}

	@Override
	public void writeChar(int v) {
		this.buf.writeChar(v);
	}

	@Override
	public void writeInt(int v) {
		this.buf.writeInt(v);
	}

	@Override
	public void writeLong(long v) {
		this.buf.writeLong(v);
	}

	@Override
	public void writeFloat(float v) {
		this.buf.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) {
		this.buf.writeDouble(v);
	}

	@Override
	public void writeBytes(@NotNull String s) {
		this.buf.writeBytes(s.getBytes());
	}

	@Override
	public void writeChars(@NotNull String s) {
		for (int i = 0; i < s.length(); i++) {
			this.writeChar(s.charAt(i));
		}
	}

	@Override
	public void writeUTF(@NotNull String s) {
		try {
			writeUTF(s, this);
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	/**
	 * copied from {@link DataOutputStream#writeUTF(String)}
	 */
	static int writeUTF(String str, DataOutput out) throws IOException {
		int strlen = str.length();
		int utflen = 0;
		int c, count = 0;

		/* use charAt instead of copying String to char array */
		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			} else if (c > 0x07FF) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}

		if (utflen > 65535)
			throw new UTFDataFormatException(
					"encoded string too long: " + utflen + " bytes");

		byte[] bytearr;
		bytearr = new byte[utflen+2];

		bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
		bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

		int i=0;
		for (i=0; i<strlen; i++) {
			c = str.charAt(i);
			if (!((c >= 0x0001) && (c <= 0x007F))) break;
			bytearr[count++] = (byte) c;
		}

		for (;i < strlen; i++){
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				bytearr[count++] = (byte) c;

			} else if (c > 0x07FF) {
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
			} else {
				bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
			}
		}
		out.write(bytearr, 0, utflen+2);
		return utflen + 2;
	}
}
