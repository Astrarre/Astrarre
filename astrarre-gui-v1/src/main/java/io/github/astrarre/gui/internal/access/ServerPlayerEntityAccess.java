package io.github.astrarre.gui.internal.access;

import java.util.Map;

import io.github.astrarre.gui.internal.comms.AbstractComms;
import io.github.astrarre.hash.v0.api.HashKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public interface ServerPlayerEntityAccess {
	Map<HashKey, AbstractComms.Server> astrarre_coms();


}
