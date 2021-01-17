package io.github.astrarre.itemview.internal.mixin.item;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.Item;

@Mixin(Item.class)
public class ItemMixin implements ItemKey {
	@Override
	public NBTagView getTag() {
		return NBTagView.EMPTY;
	}

	@Override
	public Item asItem() {
		return (Item) (Object) this;
	}
}
