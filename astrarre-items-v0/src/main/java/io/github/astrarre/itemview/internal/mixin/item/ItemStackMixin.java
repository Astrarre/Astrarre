package io.github.astrarre.itemview.internal.mixin.item;

import java.util.Objects;

import io.github.astrarre.itemview.internal.FabricViews;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.item.ItemStackView;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

@Mixin (ItemStack.class)
@Implements(@Interface(iface = ItemStackView.class, prefix = "itemview$"))
public abstract class ItemStackMixin {
	@Shadow public static native boolean areTagsEqual(ItemStack left, ItemStack right);
	@Shadow public abstract CompoundTag toTag(CompoundTag tag);
	@Shadow protected abstract boolean isEqual(ItemStack stack);
	@Shadow public abstract boolean isEmpty();
	@Shadow public abstract Item getItem();
	@Shadow public abstract int getCount();
	@Shadow public abstract int getMaxCount();
	@Shadow public abstract boolean hasTag();
	@Shadow public abstract CompoundTag getTag();
	@Shadow public abstract CompoundTag getSubTag(String key);

	@Shadow private CompoundTag tag;// @formatter:off
	@Intrinsic public boolean itemview$isEmpty(){return this.isEmpty();}
	@Intrinsic public Item itemview$getItem(){return this.getItem();}
	@Intrinsic public int itemview$getCount(){return this.getCount();}
	@Intrinsic public int itemview$getMaxCount(){return this.getMaxCount();}
	@Intrinsic public boolean itemview$hasTag(){return this.hasTag();}
	public NBTagView itemview$toTag(int count){
		CompoundTag tag = this.toTag(new CompoundTag());
		tag.putByte("Count", (byte) count);
		return FabricViews.view(tag);
	}
	public ItemStackView itemview$copy(){return FabricViews.immutableView((ItemStack) (Object) this);}
	public boolean itemview$areTagsEqual(ItemKey view){return Objects.equals(this.tag, FabricViews.fromUnsafe(view.getTag()));}
	public boolean itemview$areTagsEqual(ItemStack stack){return areTagsEqual((ItemStack) (Object) this, stack);}
	public boolean itemview$equals(ItemStackView view){return this.itemview$equals(FabricViews.fromUnsafe(view));}
	public boolean itemview$equals(ItemStack stack){return this.isEqual(stack);}
	public boolean itemview$canStackWith(ItemKey view){return this.getItem() == view.getItem() && this.itemview$areTagsEqual(view);}
	public boolean itemview$canStackWith(ItemStack stack){return this.itemview$canStackWith(FabricViews.view(stack));}
	public @Nullable NBTagView itemview$getTag(){return FabricViews.view(this.getTag());}
	public @Nullable NBTagView itemview$getSubTag(String key){return FabricViews.view(this.getSubTag(key));}
	// @formatter:on
}
