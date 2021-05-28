package io.github.astrarre.components.v0.fabric;

import io.github.astrarre.components.internal.lazyAsm.DataObjectHolderComponentFactory;
import io.github.astrarre.components.v0.api.factory.ComponentFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;

/**
 * @implNote separating the component factories by behavior instead of telling the factory behavior wasn't just an API decision, it's also better for performance
 */
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

	// todo for ItemStack
	// create a custom map from String -> Tag that creates and caches tags for the Components, requires callback to invalidate cached tags.
	// non-string objects will unfortunately need to be reserialized every time it's accessed because they may be mutable :sad_tater:
	// then, mixin to CompoundTag and check if the map is an instance of our special map, if so, optimize by not grabbing the tag
}
