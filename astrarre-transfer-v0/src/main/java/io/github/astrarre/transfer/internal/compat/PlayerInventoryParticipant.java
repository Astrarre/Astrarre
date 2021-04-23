package io.github.astrarre.transfer.internal.compat;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.player.PlayerParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class PlayerInventoryParticipant extends InventoryParticipant implements PlayerParticipant {
	public final PlayerInventory player;
	public PlayerInventoryParticipant(PlayerInventory inventory) {
		super(new ProperPlayerInventory(inventory));
		this.player = inventory;
	}

	@Override
	public ReplacingParticipant<ItemKey> getHandReplacingParticipant(Hand hand) {
		int slot;
		if(hand == Hand.MAIN_HAND) {
			slot = this.player.selectedSlot;
		} else {
			PlayerInventory inventory = this.player;
			slot = inventory.main.size() + inventory.armor.size();
		}
		return this.getSlotReplacingParticipant(slot);
	}

	@Override
	public ReplacingParticipant<ItemKey> getCursorItemReplacingParticipant() {
		return ReplacingParticipant.of(new ItemSlotParticipant(new CursorKey(this.player)) {}, this);
	}

	@Override
	public void insertOrDrop(@Nullable Transaction transaction, ItemKey key, int amount) {
		// todo world drop participant thing
	}

	public static final class CursorKey extends ObjectKeyImpl<ItemStack> {
		public final PlayerInventory inventory;

		public CursorKey(PlayerInventory inventory) {this.inventory = inventory;}

		@Override
		protected void setRootValue(ItemStack val) {
			this.inventory.setCursorStack(val);
		}

		@Override
		protected ItemStack getRootValue() {
			return this.inventory.getCursorStack();
		}
	}
}
