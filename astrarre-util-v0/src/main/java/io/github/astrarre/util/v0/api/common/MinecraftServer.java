package io.github.astrarre.util.v0.api.common;

import net.minecraft.entity.player.Player;
import net.minecraft.level.Level;

public interface MinecraftServer {
	Iterable<Player> getPlayers();
}
