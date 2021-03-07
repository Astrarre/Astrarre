package io.github.astrarre.networking.v0.api.io;

import java.io.DataOutput;

import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.NotNull;

public interface Output extends DataOutput {
	default void write(int[] arr, int off, int len) {
		for (int i = 0; i < len; i++) {
			this.writeInt(arr[off + i]);
		}
	}

	default void write(long[] arr, int off, int len) {
		for (int i = 0; i < len; i++) {
			this.writeLong(arr[off + i]);
		}
	}

	default void writeId(Id id) {
		this.writeUTF(id.id());
		this.writeUTF(id.path());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	void write(int b);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void write(byte[] b);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void write(byte[] b, int off, int len);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeBoolean(boolean v);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeByte(int v);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeShort(int v);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeChar(int v);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeInt(int v);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeLong(long v);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeFloat(float v);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeDouble(double v);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeBytes(@NotNull String s);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeChars(@NotNull String s);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void writeUTF(@NotNull String s);

	default void writeEnum(Enum<?> type) {
		this.writeInt(type.ordinal());
	}
}