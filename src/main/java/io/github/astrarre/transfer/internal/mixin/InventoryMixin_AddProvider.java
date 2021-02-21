package io.github.astrarre.transfer.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.inventory.Inventory;

@Mixin (Inventory.class)
public interface InventoryMixin_AddProvider {
	@Shadow
	int size();
}
