package io.github.astrarre.itemview.internal.mixin.item;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.Item;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemKey {
	@Shadow public abstract int getMaxCount();

	@NotNull
	@Override
	public NBTagView getTag() {
		return NBTagView.EMPTY;
	}

	@Override
	public int getMaxStackSize() {
		return this.getMaxCount();
	}

	@Override
	public Item asItem() {
		return (Item) (Object) this;
	}
}
