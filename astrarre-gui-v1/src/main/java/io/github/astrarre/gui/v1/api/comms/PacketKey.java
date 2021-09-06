package io.github.astrarre.gui.v1.api.comms;

import io.github.astrarre.hash.v0.api.HashKey;
import io.github.astrarre.hash.v0.api.Hasher;
import io.github.astrarre.hash.v0.api.SHA256Hasher;
import io.github.astrarre.util.v0.api.Id;

public abstract class PacketKey {
	public final Id id;
	public PacketKey(Id id) {
		this.id = id;
	}

	public HashKey hash() {
		try(SHA256Hasher hasher = SHA256Hasher.getPooled()) {
			this.hash0(hasher);
			return hasher.hashC();
		}
	}

	public final void hash(Hasher hasher) {
		hasher.putIdentifier(this.id);
		this.hash0(hasher);
	}

	protected abstract void hash0(Hasher hasher);
}
