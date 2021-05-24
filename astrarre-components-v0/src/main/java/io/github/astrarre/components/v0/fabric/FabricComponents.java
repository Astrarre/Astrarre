package io.github.astrarre.components.v0.fabric;

import io.github.astrarre.components.internal.lazyAsm.DataObjectHolderComponentFactory;
import io.github.astrarre.components.v0.api.factory.ComponentFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;

public class FabricComponents {
	/**
	 * Not copied when the entity moves dimensions or cloned.
	 * Not serialized/deserialized from NBT.
	 */
	public static final ComponentFactory<Entity> ENTITY_NO_COPY_NO_SERIALIZE = new DataObjectHolderComponentFactory<>("astrarre-components", "entity_no_copy");
	/**
	 * Not serialized/deserialized from NBT.
	 * Not invalidated if the block entity is invalidated and re-validated.
	 */
	public static final ComponentFactory<BlockEntity> BLOCK_ENTITY_NO_SERIALIZE = new DataObjectHolderComponentFactory<>("astrarre-components", "entity_no_copy");

}
