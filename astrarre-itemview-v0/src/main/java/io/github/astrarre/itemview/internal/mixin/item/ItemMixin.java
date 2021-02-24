package io.github.astrarre.itemview.internal.mixin.item;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.Item;

@Mixin(Item.class)
public abstract class ItemMixin implements TaggedItem {
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
	public Item getItem() {
		return (Item) (Object) this;
	}
}