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

	private static final Id INT = Id.create("astrarre", "packet_key_int");
	/**
	 * A packet key based on integer id, I would only recommend using this occasionally, and never from inside components.
	 */
	public static final class Int extends PacketKey {
		private final int val;
		public Int(int val) {
			super(INT);
			this.val = val;
		}

		@Override
		protected void hash0(Hasher hasher) {
			hasher.putInt(this.val);
		}
	}
}
