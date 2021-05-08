package io.github.astrarre.access.v0.forge;

import io.github.astrarre.access.v0.api.Access;
import net.minecraftforge.eventbus.api.Event;

public class AccessInitEvent extends Event {
	public final Access<?> access;
	public AccessInitEvent(Access<?> access) {
		this.access = access;
	}
}
