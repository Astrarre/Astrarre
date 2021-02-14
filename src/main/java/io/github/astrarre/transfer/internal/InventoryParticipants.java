package io.github.astrarre.transfer.internal;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.AggregateParticipant;

import net.minecraft.inventory.Inventory;

public class InventoryParticipants {
	// todo dynamic inventory support
	public static AggregateParticipant<ItemKey> get(Inventory inventory) {
		Participant<ItemKey>[] list = new Participant[inventory.size()];
		for (int i = 0; i < inventory.size(); i++) {
			list[i] = new SlotParticipant(inventory, i);
		}
		return new AggregateParticipant<>(list);
	}
}
