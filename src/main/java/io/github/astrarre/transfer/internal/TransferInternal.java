package io.github.astrarre.transfer.internal;

import io.github.astrarre.access.internal.inventory.SidedInventoryAccess;
import io.github.astrarre.access.v0.api.BiFunctionAccess;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import io.github.astrarre.transfer.internal.inventory.AggregateParticipantInventory;
import io.github.astrarre.transfer.internal.inventory.ParticipantInventory;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.AggregateParticipant;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

public class TransferInternal {
	public static final FunctionAccess<Participant<TaggedItem>, Inventory> TO_INVENTORY = new FunctionAccess<>();
	
	/**
	 * register conversion functions here
	 */
	public static final BiFunctionAccess<Direction, Inventory, Participant<TaggedItem>> FROM_INVENTORY_REGISTRY = new BiFunctionAccess<>();

	/**
	 * this is where you should access to convert, this contains astrarre's standard converters.
	 */
	public static final BiFunctionAccess<Direction, Inventory, Participant<TaggedItem>> FROM_INVENTORY = new BiFunctionAccess<>();


	public static long version = 0;

	static {
		TO_INVENTORY.addProviderFunction();
		TO_INVENTORY.andThen(participant -> {
			if (participant instanceof AggregateParticipant) {
				return new AggregateParticipantInventory((AggregateParticipant<TaggedItem>) participant);
			}
			return null;
		});
		TO_INVENTORY.andThen(ParticipantInventory::new);

		FROM_INVENTORY.andThen((direction, inventory) -> {
			if(inventory instanceof ParticipantInventory) {
				return ((ParticipantInventory) inventory).participant;
			}

			if (inventory instanceof SidedInventory) {
				inventory = new SidedInventoryAccess((SidedInventory) inventory, direction);
			}

			return get(inventory);
		});
	}

	private static Participant<TaggedItem> get(Inventory inventory) {
		Participant<TaggedItem>[] list = new Participant[inventory.size()];
		for (int i = 0; i < inventory.size(); i++) {
			list[i] = new SlotParticipant(inventory, i);
		}
		// todo inventory aggregate participant to avoid wrapping and re-wrapping inventory
		// also we need some kind of caching thing tbh
		return new AggregateParticipant<>(list);
	}
}
