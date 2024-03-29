package io.github.astrarre.gui.internal.std;

import io.github.astrarre.gui.v1.api.comms.PacketKey;
import io.github.astrarre.hash.v0.api.Hasher;
import io.github.astrarre.util.v0.api.Id;

public class SingleUsePacket extends PacketKey {
	private static final Id OPEN = Id.create("astrarre", "use");
	public SingleUsePacket() {
		super(OPEN);
	}

	@Override
	protected void hash0(Hasher hasher) {
	}
}
