package io.github.astrarre.itemview.internal.astrarre.mixin;

import io.github.astrarre.itemview.internal.TaggedItemImpl;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.Item;

@Mixin(TaggedItemImpl.class)
public abstract class ItemKeyImplMixin implements ItemKey {
	@Override
	@Shadow public abstract Item getItem();

	@Override
	public io.github.astrarre.v0.item.Item asItem() {
		return (io.github.astrarre.v0.item.Item) this.getItem();
	}
}
