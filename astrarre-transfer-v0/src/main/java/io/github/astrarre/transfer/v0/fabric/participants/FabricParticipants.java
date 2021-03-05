package io.github.astrarre.transfer.v0.fabric.participants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.astrarre.access.v0.api.BiFunctionAccess;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.api.func.AccessFunction;
import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import io.github.astrarre.transfer.internal.fabric.NUtil;
import io.github.astrarre.transfer.internal.fabric.SlotParticipant;
import io.github.astrarre.transfer.internal.fabric.inventory.CombinedSidedInventory;
import io.github.astrarre.transfer.internal.fabric.inventory.EmptyInventory;
import io.github.astrarre.transfer.internal.fabric.inventory.SidedInventoryAccess;
import io.github.astrarre.transfer.internal.fabric.participantInventory.AggregateParticipantInventory;
import io.github.astrarre.transfer.internal.fabric.participantInventory.ParticipantInventory;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.api.participants.AggregateParticipant;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;
import io.github.astrarre.transfer.v0.api.participants.ObjectVolume;
import io.github.astrarre.transfer.v0.fabric.participants.item.ItemSlotParticipant;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * get your inventories from {@link HopperBlockEntity#getInventoryAt(World, BlockPos)}
 */
public final class FabricParticipants {
	/**
	 * if a participant is looking for a limited set of items, this can help narrow it down
	 */
	public static final FunctionAccess<Insertable<TaggedItem>, Set<Item>> FILTERS = FunctionAccess.newInstance(sets -> {
		Set<Item> combined = new HashSet<>();
		sets.forEach(combined::addAll);
		return combined;
	});

	/**
	 * This can be used to extract fluids from items, the participant is where items are dumped or extracted from. For example, a water bucket will
	 * try to insert empty buckets into the participant if it is emptied.
	 *
	 * todo just go with the direct access instead of trying to heuristically find the items
	 */
	public static final BiFunctionAccess<TaggedItem, Participant<TaggedItem>, Participant<Fluid>> ITEM_FLUID =
			BiFunctionAccess.newInstance(AggregateParticipant::merge);

	public static final FunctionAccess<Participant<TaggedItem>, Inventory> TO_INVENTORY = new FunctionAccess<>();

	/**
	 * this is where you should access to convert, this contains astrarre's standard converters.
	 */
	public static final BiFunctionAccess<Direction, Inventory, Participant<TaggedItem>> FROM_INVENTORY = new BiFunctionAccess<>();

	static {
		TO_INVENTORY.addProviderFunction();
		TO_INVENTORY.andThen(participant -> {
			if (participant instanceof AggregateParticipant) {
				return new AggregateParticipantInventory((AggregateParticipant<TaggedItem>) participant);
			}
			return null;
		});

		// todo voiding and creative sink
		TO_INVENTORY.forInstance(Participants.EMPTY.cast(), participant -> EmptyInventory.INSTANCE);

		TO_INVENTORY.andThen(ParticipantInventory::new);

		FROM_INVENTORY.andThen((direction, inventory) -> {
			if(inventory instanceof ParticipantInventory) {
				return ((ParticipantInventory) inventory).participant;
			}

			if (inventory instanceof SidedInventory) {
				inventory = new SidedInventoryAccess((SidedInventory) inventory, direction);
			}

			if(inventory instanceof AggregateParticipantInventory) {
				return ((AggregateParticipantInventory) inventory).participant;
			}

			Participant<TaggedItem>[] list = new Participant[inventory.size()];
			for (int i = 0; i < inventory.size(); i++) {
				list[i] = new SlotParticipant(inventory, i);
			}
			// todo inventory aggregate participant to avoid wrapping and re-wrapping inventory
			// also we need some kind of caching thing tbh
			return new AggregateParticipant<>(list);
		});
	}

	public static ObjectVolume<Fluid> createFluidVolume(Fluid fluid, int quantity) {
		return new ObjectVolume<>(Fluids.EMPTY, fluid, quantity);
	}

	public static ObjectVolume<Fluid> createFluidVolume() {
		return new ObjectVolume<>(Fluids.EMPTY);
	}

	/**
	 * @return a new object volume for fluids that has a maximum size
	 */
	public static FixedObjectVolume<Fluid> createFixedFluidVolume(Fluid fluid, int quantity, int max) {
		return new FixedObjectVolume<>(Fluids.EMPTY, fluid, quantity, max);
	}

	public static FixedObjectVolume<Fluid> createFixedFluidVolume(int max) {
		return new FixedObjectVolume<>(Fluids.EMPTY, max);
	}

	/**
	 * @return a new object volume for items that respects its max stack size
	 */
	public static ItemSlotParticipant createItemVolume() {
		return new ItemSlotParticipant();
	}

	public static ItemSlotParticipant createItemVolume(TaggedItem key, int quantity) {
		return new ItemSlotParticipant(key, quantity);
	}

	public static SidedInventory create(Inventory bottom, Inventory top, Inventory north, Inventory south, Inventory west, Inventory east) {
		return new CombinedSidedInventory(bottom, top, north, south, west, east);
	}

	static {
		FILTERS.addProviderFunction();
		FILTERS.dependsOn(Participants.AGGREGATE_WRAPPERS_INSERTABLE, function -> insertable -> {
			Collection<Insertable<TaggedItem>> wrapped = Participants.unwrapInternal((AccessFunction) function, insertable);
			if (wrapped == null) {
				return Collections.emptySet();
			}

			Set<Item> combined = null;
			for (Insertable<TaggedItem> delegate : wrapped) {
				combined = NUtil.addAll(combined, FILTERS.get().apply(delegate));
			}
			return combined;
		});
	}
}
