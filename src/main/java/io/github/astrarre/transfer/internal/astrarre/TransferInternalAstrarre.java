package io.github.astrarre.transfer.internal.astrarre;


import io.github.astrarre.access.v0.api.WorldAccess;
import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.internal.fabric.inventory.CombinedSidedInventory;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.block.entity.BlockEntity;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.util.math.Direction;
import io.github.astrarre.v0.world.World;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.InventoryProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;

public class TransferInternalAstrarre {
	public static final WorldAccess<Participant<ItemKey>> ITEM_WORLD = new WorldAccess<>(Participants.EMPTY.cast());

	static {
		ITEM_WORLD.andThen((direction, state, view, pos, entity) -> {
			if (entity instanceof Inventory) {
				// noinspection rawtypes,unchecked,ConstantConditions
				return (Participant) FabricParticipants.FROM_INVENTORY.get()
						                     .apply((net.minecraft.util.math.Direction) (Object) direction, (Inventory) entity);
			}
			return null;
		});

		ITEM_WORLD.andThen((WorldFunction.NoBlockEntity<Participant<ItemKey>>) (d, s, v, p) -> {
			if (s.getBlock() instanceof InventoryProvider) {
				InventoryProvider block = (InventoryProvider) s.getBlock();
				// noinspection rawtypes,unchecked,ConstantConditions
				return (Participant)FabricParticipants.FROM_INVENTORY.get().apply(
						(net.minecraft.util.math.Direction) (Object) d,
						block.getInventory((net.minecraft.block.BlockState) s, (net.minecraft.world.World) v, (net.minecraft.util.math.BlockPos) p));
			}
			return null;
		});
	}

	@SuppressWarnings ({
			"unchecked",
			"rawtypes"
	})
	public static SidedInventory getSidedInventoryAt(WorldFunction<Participant<ItemKey>> function,
			World world,
			BlockPos pos,
			BlockState state,
			@Nullable BlockEntity entity) {
		if (state == null) {
			state = world.getBlockState(pos);
		}

		if (entity == null && state.getBlock().hasBlockEntity()) {
			entity = world.getBlockEntity(pos);
		}
		return new CombinedSidedInventory(
				FabricParticipants.TO_INVENTORY.get().apply((Participant) function.get(Direction.UP, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply((Participant) function.get(Direction.DOWN, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply((Participant) function.get(Direction.NORTH, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply((Participant) function.get(Direction.SOUTH, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply((Participant) function.get(Direction.WEST, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get().apply((Participant) function.get(Direction.EAST, state, world, pos, entity)));
	}

	public static void init() {}
}
