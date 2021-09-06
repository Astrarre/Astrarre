package io.github.astrarre.gui.internal.mixin;

import java.util.HashMap;
import java.util.Map;

import io.github.astrarre.gui.internal.access.ServerPlayerEntityAccess;
import io.github.astrarre.gui.internal.comms.AbstractComms;
import io.github.astrarre.hash.v0.api.HashKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin_Access implements ServerPlayerEntityAccess {
	final Map<HashKey, AbstractComms.Server> astrarre_coms = new HashMap<>();

	@Override
	public Map<HashKey, AbstractComms.Server> astrarre_coms() {
		return astrarre_coms;
	}


}
