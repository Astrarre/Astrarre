package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.internal.access.PlayerEntityAccess;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin_Access implements PlayerEntityAccess {
	int syncId;

	@Override
	public int astrarre_syncId() {
		return syncId++;
	}
}
