package io.github.astrarre.transfer.v0.fabric.participants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.astrarre.access.v0.api.BiFunctionAccess;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.api.func.AccessFunction;
import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.NUtil;
import io.github.astrarre.transfer.internal.SlotParticipant;
import io.github.astrarre.transfer.internal.compat.ProperPlayerInventory;
import io.github.astrarre.transfer.v0.fabric.inventory.CombinedSidedInventory;
import io.github.astrarre.transfer.v0.fabric.inventory.EmptyInventory;
import io.github.astrarre.transfer.v0.fabric.inventory.SidedInventoryAccess;
import io.github.astrarre.transfer.internal.participantInventory.ParticipantInventory;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.api.participants.AggregateParticipant;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;
import io.github.astrarre.transfer.v0.api.participants.ObjectVolume;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * get your inventories from {@link HopperBlockEntity#getInventoryAt(World, BlockPos)}
 */
public final class FabricParticipants {
	public static final Serializer<ObjectVolume<Fluid>> FLUID_OBJECT_VOLUME_SERIALIZER = ObjectVolume.serializer(Fluids.EMPTY, FabricSerializers.of(Registry.FLUID));
	public static final Serializer<FixedObjectVolume<Fluid>> FLUID_FIXED_OBJECT_VOLUME_SERIALIZER = FixedObjectVolume.fixedSerializer(Fluids.EMPTY, FabricSerializers.of(Registry.FLUID));
	public static final WorldAccess<Participant<ItemKey>> ITEM_WORLD = new WorldAccess<>(Participants.EMPTY.cast());
	public static final WorldAccess<Participant<Fluid>> FLUID_WORLD = new WorldAccess<>(Participants.EMPTY.cast());

	/**
	 * if an insertable is looking for a limited set of items, this can help narrow it down
	 */
	public static final FunctionAccess<Insertable<ItemKey>, Set<Item>> FILTERS = FunctionAccess.newInstance(sets -> {
		Set<Item> combined = new HashSet<>();
		sets.forEach(combined::addAll);
		return combined;
	});

	public static final FunctionAccess<Participant<ItemKey>, Inventory> TO_INVENTORY = new FunctionAccess<>();

	/**
	 * this is where you should access to convert, this contains astrarre's standard converters.
	 */
	public static final BiFunctionAccess<Direction, Inventory, Participant<ItemKey>> FROM_INVENTORY = new BiFunctionAccess<>();

	static {
		ITEM_WORLD.addWorldProviderFunctions();
		FLUID_WORLD.addWorldProviderFunctions();
		TO_INVENTORY.addProviderFunction();

		// todo voiding and creative sink
		TO_INVENTORY.forInstance(Participants.EMPTY.cast(), participant -> EmptyInventory.INSTANCE);

		TO_INVENTORY.andThen(ParticipantInventory::new);

		FROM_INVENTORY.andThen((direction, inventory) -> {
			if(inventory instanceof ParticipantInventory) {
				return ((ParticipantInventory) inventory).participant;
			}

			if(inventory instanceof PlayerInventory) {
				inventory = new ProperPlayerInventory((PlayerInventory) inventory);
			}

			if (inventory instanceof SidedInventory) {
				inventory = new SidedInventoryAccess((SidedInventory) inventory, direction);
			}

			Participant<ItemKey>[] list = new Participant[inventory.size()];
			for (int i = 0; i < inventory.size(); i++) {
				list[i] = new SlotParticipant(inventory, i);
			}
			// todo inventory aggregate participant to avoid wrapping and re-wrapping inventory
			// also we need some kind of caching thing tbh
			return new AggregateParticipant<>(list);
		});

		FILTERS.addProviderFunction();
		FILTERS.dependsOn(Participants.AGGREGATE_WRAPPERS_INSERTABLE, function -> insertable -> {
			Collection<Insertable<ItemKey>> wrapped = Participants.unwrapInternal((AccessFunction) function, insertable);
			if (wrapped == null) {
				return Collections.emptySet();
			}

			Set<Item> combined = null;
			for (Insertable<ItemKey> delegate : wrapped) {
				combined = NUtil.addAll(combined, FILTERS.get().apply(delegate));
			}
			return combined;
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

	public static ItemSlotParticipant createItemVolume(ItemKey key, int quantity) {
		return new ItemSlotParticipant(key, quantity);
	}

	public static SidedInventory create(Inventory bottom, Inventory top, Inventory north, Inventory south, Inventory west, Inventory east) {
		return new CombinedSidedInventory(bottom, top, north, south, west, east);
	}

	public static SidedInventory getSidedInventoryAt(WorldFunction<Participant<ItemKey>> function,
			World world,
			BlockPos pos,
			@Nullable BlockState state,
			@Nullable BlockEntity entity) {
		if (state == null) {
			state = world.getBlockState(pos);
		}

		if (entity == null && state.getBlock().hasBlockEntity()) {
			entity = world.getBlockEntity(pos);
		}

		return new CombinedSidedInventory(FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.UP, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.DOWN, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.NORTH, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.SOUTH, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.WEST, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.EAST, state, world, pos, entity)));
	}
}
