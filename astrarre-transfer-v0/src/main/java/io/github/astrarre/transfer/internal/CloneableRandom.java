package io.github.astrarre.transfer.internal;

import java.util.Random;

import io.github.astrarre.util.v0.api.Validate;

public final class CloneableRandom extends Random implements Cloneable {
	public CloneableRandom() {
	}

	public CloneableRandom(long seed) {
		super(seed);
	}

	@Override
	public CloneableRandom clone() {
		try {
			return (CloneableRandom) super.clone();
		} catch(CloneNotSupportedException e) {
			throw Validate.rethrow(e);
		}
	}
}