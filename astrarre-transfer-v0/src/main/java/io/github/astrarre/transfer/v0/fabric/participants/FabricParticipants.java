package io.github.astrarre.transfer.v0.fabric.participants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import io.github.astrarre.access.v0.api.BiFunctionAccess;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.fabric.EntityAccess;
import io.github.astrarre.access.v0.fabric.ItemAccess;
import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.NUtil;
import io.github.astrarre.transfer.internal.compat.BucketItemParticipant;
import io.github.astrarre.transfer.internal.compat.CauldronParticipant;
import io.github.astrarre.transfer.internal.compat.FishBucketItemParticipant;
import io.github.astrarre.transfer.internal.compat.InventoryParticipant;
import io.github.astrarre.transfer.internal.compat.PlayerInventoryParticipant;
import io.github.astrarre.transfer.internal.compat.ProperPlayerInventory;
import io.github.astrarre.transfer.internal.compat.ShulkerboxItemParticipant;
import io.github.astrarre.transfer.internal.participantInventory.ArrayParticipantInventory;
import io.github.astrarre.transfer.internal.participantInventory.ParticipantInventory;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;
import io.github.astrarre.transfer.v0.api.participants.ObjectVolume;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.player.PlayerParticipant;
import io.github.astrarre.transfer.v0.fabric.inventory.CombinedSidedInventory;
import io.github.astrarre.transfer.v0.fabric.inventory.EmptyInventory;
import io.github.astrarre.transfer.v0.fabric.inventory.SidedInventoryAccess;
import io.github.astrarre.transfer.v0.fabric.inventory.VoidingInventory;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FishBucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * get your inventories from {@link HopperBlockEntity#getInventoryAt(World, BlockPos)}
 */
public final class FabricParticipants {
	public static final Serializer<ObjectVolume<Fluid>> FLUID_OBJECT_VOLUME_SERIALIZER = ObjectVolume.serializer(Fluids.EMPTY,
			FabricSerializers.of(Registry.FLUID));
	public static final Serializer<FixedObjectVolume<Fluid>> FLUID_FIXED_OBJECT_VOLUME_SERIALIZER = FixedObjectVolume.fixedSerializer(Fluids.EMPTY,
			FabricSerializers.of(Registry.FLUID));
	public static final WorldAccess<Participant<ItemKey>> ITEM_WORLD = new WorldAccess<>(id("item_world"), Participants.EMPTY.cast());
	public static final WorldAccess<Participant<Fluid>> FLUID_WORLD = new WorldAccess<>(id("fluid_world"), Participants.EMPTY.cast());
	public static final EntityAccess<Participant<ItemKey>> ITEM_ENTITY = new EntityAccess<>(id("item_entity"), Participants.EMPTY.cast());
	public static final EntityAccess<Participant<Fluid>> FLUID_ENTITY = new EntityAccess<>(id("fluid_entity"), Participants.EMPTY.cast());

	/**
	 * get item container from item in an item container
	 */
	public static final ItemAccess<Participant<ItemKey>, ReplacingParticipant<ItemKey>> ITEM_ITEM = new ItemAccess<>(id("item_item"), Participants.EMPTY.cast());
	/**
	 * get fluid container from fluid in an item container
	 */
	public static final ItemAccess<Participant<Fluid>, ReplacingParticipant<ItemKey>> FLUID_ITEM = new ItemAccess<>(id("fluid_item"),Participants.EMPTY.cast());

	/**
	 * if an insertable is looking for a limited set of items, this can help narrow it down
	 */
	public static final FunctionAccess<Insertable<ItemKey>, Set<Item>> ITEM_FILTERS = FunctionAccess.newInstance(id("item_filters"), sets -> {
		Set<Item> combined = new HashSet<>();
		sets.forEach(combined::addAll);
		return combined;
	});

	public static final FunctionAccess<Insertable<ItemKey>, Set<Fluid>> FLUID_FILTERS = FunctionAccess.newInstance(id("fluid_filters"), sets -> {
		Set<Fluid> combined = new HashSet<>();
		sets.forEach(combined::addAll);
		return combined;
	});

	public static final FunctionAccess<Participant<ItemKey>, Inventory> TO_INVENTORY = new FunctionAccess<>(id("to_inventory"));

	/**
	 * this is where you should access to convert, this contains astrarre's standard converters.
	 */
	public static final BiFunctionAccess<Direction, Inventory, Participant<ItemKey>> FROM_INVENTORY = new BiFunctionAccess<>(id("from_inventory"));

	protected static final Map<PlayerInventory, PlayerInventoryParticipant> PLAYER_INVENTORY_PARTICIPANT_MAP = new WeakHashMap<>();
	public static PlayerParticipant forPlayerInventory(PlayerInventory inventory) {
		// todo maybe store on player inventory
		return PLAYER_INVENTORY_PARTICIPANT_MAP.computeIfAbsent(inventory, PlayerInventoryParticipant::new);
	}

	static {
		ITEM_WORLD.addWorldProviderFunctions();
		FLUID_WORLD.addWorldProviderFunctions();
		TO_INVENTORY.addProviderFunction();
		ITEM_ENTITY.addEntityProviderFunction();
		FLUID_ENTITY.addEntityProviderFunction();
		ITEM_ITEM.addItemProviderFunctions();
		FLUID_ITEM.addItemProviderFunctions();

		ITEM_ITEM.forBlockItemClassExact(ShulkerBoxBlock.class, (direction, key, count, participant) -> {
			if (count == 1) {
				return ShulkerboxItemParticipant.create(participant, key, BlockEntityType.SHULKER_BOX);
			}
			return null;
		});

		FLUID_ITEM.forItemClassExact(BucketItem.class, (direction, key, count, participant) -> new BucketItemParticipant(key, count, participant));
		FLUID_ITEM.forItemClassExact(FishBucketItem.class, (direction, key, count, participant) -> new FishBucketItemParticipant(key, count, participant));

		FLUID_WORLD.forBlock(Blocks.CAULDRON, (WorldFunction.NoBlockEntity<Participant<Fluid>>) (direction, state, world, pos) -> new CauldronParticipant(state, world, pos));
		TO_INVENTORY.forInstance(Participants.EMPTY.cast(), participant -> EmptyInventory.INSTANCE);
		TO_INVENTORY.forInstance(Participants.VOIDING.cast(), participant -> VoidingInventory.INSTANCE);

		TO_INVENTORY.andThen(participant -> {
			if (participant instanceof ArrayParticipant) {
				return new ArrayParticipantInventory((ArrayParticipant<ItemKey>) participant);
			}
			return null;
		});

		TO_INVENTORY.andThen(ParticipantInventory::new);
		FROM_INVENTORY.andThen((direction, inventory) -> {
			if (inventory instanceof EmptyInventory) {
				return Participants.EMPTY.cast();
			}

			if (inventory instanceof VoidingInventory) {
				return Participants.VOIDING.cast();
			}

			if (inventory instanceof ParticipantInventory) {
				return ((ParticipantInventory) inventory).participant;
			}

			if (inventory instanceof PlayerInventory) {
				return new PlayerInventoryParticipant((PlayerInventory) inventory);
			}

			if (inventory instanceof SidedInventory) {
				inventory = new SidedInventoryAccess((SidedInventory) inventory, direction);
			}

			return new InventoryParticipant(inventory);
		});

		FLUID_FILTERS.addProviderFunction();
		// todo optimize, this should stop at the first wrapper that actually implements the access
		FLUID_FILTERS.dependsOn(Participants.AGGREGATE_WRAPPERS_INSERTABLE, function -> insertable -> {
			Collection<Insertable<ItemKey>> wrapped = Participants.unwrapInternal((Function) function, insertable, true);
			if (wrapped == null) {
				return Collections.emptySet();
			}

			Set<Fluid> combined = null;
			for (Insertable<ItemKey> delegate : wrapped) {
				combined = NUtil.addAll(combined, FLUID_FILTERS.get().apply(delegate));
			}
			return combined;
		});

		ITEM_FILTERS.addProviderFunction();
		ITEM_FILTERS.dependsOn(Participants.AGGREGATE_WRAPPERS_INSERTABLE, function -> insertable -> {
			Collection<Insertable<ItemKey>> wrapped = Participants.unwrapInternal((Function) function, insertable, true);
			if (wrapped == null) {
				return Collections.emptySet();
			}

			Set<Item> combined = null;
			for (Insertable<ItemKey> delegate : wrapped) {
				combined = NUtil.addAll(combined, ITEM_FILTERS.get().apply(delegate));
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

		return create(FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.UP, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.DOWN, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.NORTH, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.SOUTH, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.WEST, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply(function.get(Direction.EAST, state, world, pos, entity)),
				true);
	}

	public static SidedInventory create(Inventory bottom,
			Inventory top,
			Inventory north,
			Inventory south,
			Inventory west,
			Inventory east,
			boolean cache) {
		return new CombinedSidedInventory(ImmutableMap.<Direction, Inventory>builder().put(Direction.UP, (top))
		                                                                              .put(Direction.DOWN, (bottom))
		                                                                              .put(Direction.NORTH, (north))
		                                                                              .put(Direction.EAST, (east))
		                                                                              .put(Direction.SOUTH, (south))
		                                                                              .put(Direction.WEST, (west))
		                                                                              .build(), cache);
	}

	private static Id id(String name) {
		return Id.create("astrarre-transfer-v0", name);
	}
}
