package io.github.astrarre.gui.internal.mixin;

import java.util.List;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ExtraSlotAccess;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.containers.ScreenHandlerContainer;
import io.github.astrarre.gui.internal.vanilla.NilSlot;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin_ScreenRootAccessImpl implements ScreenRootAccess {
	@Shadow @Final public DefaultedList<Slot> slots;
	@Shadow @Final private DefaultedList<ItemStack> trackedStacks;
	private RootContainerInternal internal;

	@Override
	public RootContainerInternal getRoot() {
		if(this.internal == null) {
			this.internal = new ScreenHandlerContainer((ScreenHandler) (Object) this);
		}
		return this.internal;
	}

	@Inject(method = "addSlot", at = @At("HEAD"), cancellable = true)
	public void addSlot(Slot slot, CallbackInfoReturnable<Slot> cir) {
		if (slot instanceof ExtraSlotAccess && ((ExtraSlotAccess) slot).idOverride() != -1) {
			int id = ((ExtraSlotAccess) slot).idOverride();
			int desired = id - this.slots.size();
			for (int i = 0; i < desired+1; i++) {
				this.slots.add(NilSlot.SLOT);
				this.trackedStacks.add(ItemStack.EMPTY);
			}

			this.slots.set(id, slot);
			this.trackedStacks.set(id, ItemStack.EMPTY);
			cir.setReturnValue(slot);
		}
	}

	@Override
	public void readRoot(NBTagView input) {
		this.internal = new ScreenHandlerContainer((ScreenHandler) (Object) this, input);
	}

	@Override
	public void astrarre_focusPanel() {
		throw new UnsupportedOperationException();
	}

	@Inject(method = "close", at = @At("HEAD"))
	public void close(PlayerEntity player, CallbackInfo ci) {
		if(this.internal != null) {
			this.internal.onClose();
		}
	}
}
