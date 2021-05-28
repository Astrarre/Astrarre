package io.github.astrarre.transfer.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.EntityType;
import net.minecraft.item.EntityBucketItem;

@Mixin (EntityBucketItem.class)
public interface FishBucketItemAccess_EntityTypeAccess {
	@Accessor
	EntityType<?> getEntityType();
}
