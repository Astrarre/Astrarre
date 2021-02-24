package io.github.astrarre.itemview.internal.astrarre.mixin;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.Item;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemKey {
	@Override
	public io.github.astrarre.v0.item.Item asItem() {
		return (io.github.astrarre.v0.item.Item) this;
	}
}
