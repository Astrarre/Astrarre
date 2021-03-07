package io.github.astrarre.networking.v0.api.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.NotNull;

public interface Input extends DataInput {
	default <T extends Enum<T>> T readEnum(Class<T> enumClass) {
		return enumClass.getEnumConstants()[this.readInt()];
	}

	interface BufferOperator<T> {
		void set(Input input, T array, int index);
	}

	default void writeTo(OutputStream stream, byte[] buffer) throws IOException {
		int count;
		while ((count = this.read(buffer)) != -1) {
			stream.write(buffer, 0, count);
		}
	}

	default <T> int read(T array, int off, int len, BufferOperator<T> setter, int size) {
		for (int i = 0; i < len; i++) {
			if(this.bytes() < size) {
				return i;
			}
			setter.set(this, array, off + i);
		}
		return len;
	}

	/**
	 * @return the amount of longs actually read
	 */
	default int read(long[] longs, int off, int len) {
		return this.read(longs, off, len, (input, array, index) -> array[off + index] = input.readLong(), 8);
	}

	default int read(int[] ints, int off, int len) {
		return this.read(ints, off, len, (input, array, index) -> array[off + index] = input.readInt(), 4);
	}

	default Id readId() {
		return Id.create(this.readUTF(), this.readUTF());
	}

	int read(byte[] buffer);

	/**
	 * @return the amount of bytes predicted to be read
	 */
	int bytes();

	/**
	 * {@inheritDoc}
	 */
	@Override
	void readFully(byte[] b);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void readFully(byte[] b, int off, int len);

	/**
	 * {@inheritDoc}
	 */
	@Override
	int skipBytes(int n);

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean readBoolean();

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte readByte();

	/**
	 * {@inheritDoc}
	 */
	@Override
	int readUnsignedByte();

	/**
	 * {@inheritDoc}
	 */
	@Override
	short readShort();

	/**
	 * {@inheritDoc}
	 */
	@Override
	int readUnsignedShort();

	long readUnsignedInt();

	/**
	 * {@inheritDoc}
	 */
	@Override
	char readChar();

	/**
	 * {@inheritDoc}
	 */
	@Override
	int readInt();

	/**
	 * {@inheritDoc}
	 */
	@Override
	long readLong();

	/**
	 * {@inheritDoc}
	 */
	@Override
	float readFloat();

	/**
	 * {@inheritDoc}
	 */
	@Override
	double readDouble();

	/**
	 * {@inheritDoc}
	 */
	@Override
	String readLine();

	/**
	 * @see DataInputStream#readUTF()
	 * {@inheritDoc}
	 */
	@NotNull
	@Override
	String readUTF();
}
