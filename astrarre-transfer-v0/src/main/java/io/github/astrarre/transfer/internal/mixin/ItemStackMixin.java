package io.github.astrarre.transfer.internal.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.astrarre.transfer.internal.access.ItemStackAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackAccess {
	private List<Consumer<ItemStack>> list;

	@Override
	public void astrarre_onChange(Consumer<ItemStack> stack) {
		if(this.list == null)
			this.list = new ArrayList<>();
		this.list.add(stack);
	}

	@Inject(method = "setCount", at = @At("RETURN"))
	public void onSetCount(int count, CallbackInfo ci) {
		if (this.list != null) {
			for (Consumer<ItemStack> consumer : this.list) {
				consumer.accept((ItemStack) (Object) this);
			}
		}
	}
}
