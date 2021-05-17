package io.github.astrarre.transfer.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.EntityType;
import net.minecraft.item.FishBucketItem;

@Mixin (FishBucketItem.class)
public interface FishBucketItemAccess_EntityTypeAccess {
	@Accessor
	EntityType<?> getFishType();
}
