package io.github.astrarre.transfer.v0.api;

import io.github.astrarre.transfer.internal.TransferInternal;

/**
 * @see Droplet
 */
public interface Participant<T> extends Extractable<T>, Insertable<T> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	default long getVersion() {
		return TransferInternal.version++;
	}
	default boolean supportsExtraction() {
		return true;
	}
	default boolean supportsInsertion() {
		return true;
	}
}
