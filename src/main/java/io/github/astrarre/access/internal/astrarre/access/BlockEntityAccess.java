package io.github.astrarre.access.internal.astrarre.access;

import java.util.function.Consumer;

import net.minecraft.block.entity.BlockEntity;

public interface BlockEntityAccess {
	void astrarre_addRemoveOrMoveListener(Consumer<BlockEntity> consumer);

	void astrarre_invalidate();
}
