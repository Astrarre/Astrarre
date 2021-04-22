package io.github.astrarre.transfer_compat.internal;

import java.util.Collections;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.fabric.inventory.CombinedSidedInventory;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings ({
		"rawtypes",
		"unchecked"
})
public class HopperBlockEntityTransformer_InventoryCrossCompatibilityHooks {
	private static final Supplier<WorldFunction<Participant<ItemKey>>> FUNCTION = FabricParticipants.ITEM_WORLD.getExcluding(Collections.singleton(
			TransferInternalAstrarre.HOPPER_BLOCK_ENTITY_GET_INVENTORY));

	public static Inventory get(BlockPos pos, World world, BlockState state) {
		if (!state.getBlock().hasBlockEntity()) {
			return get(pos, world, state, null);
		}
		return null;
	}

	public static Inventory get(BlockPos posM, World worldM, BlockState stateM, BlockEntity entityM) {
		if (entityM instanceof Inventory) {
			return (Inventory) entityM;
		}

		WorldFunction<Participant<ItemKey>> function = FUNCTION.get();
		Participant up = function.get(Direction.UP, stateM, worldM, posM, entityM), down = function.get(
				Direction.DOWN,
				stateM,
				worldM,
				posM,
				entityM), north = function.get(Direction.NORTH, stateM, worldM, posM, entityM), south = function.get(
				Direction.SOUTH,
				stateM,
				worldM,
				posM,
				entityM), west = function.get(Direction.WEST, stateM, worldM, posM, entityM), east = function.get(
				Direction.EAST,
				stateM,
				worldM,
				posM,
				entityM);

		if (up == Participants.EMPTY && down == Participants.EMPTY && north == Participants.EMPTY && south == Participants.EMPTY && west == Participants.EMPTY && east == Participants.EMPTY) {
			return null;
		}

		return new CombinedSidedInventory(ImmutableMap.<Direction, Inventory>builder()
				                                  .put(Direction.UP, FabricParticipants.TO_INVENTORY.get().apply(up))
				                                  .put(Direction.DOWN, FabricParticipants.TO_INVENTORY.get().apply(down))
				                                  .put(Direction.NORTH, FabricParticipants.TO_INVENTORY.get().apply(north))
				                                  .put(Direction.EAST, FabricParticipants.TO_INVENTORY.get().apply(east))
				                                  .put(Direction.SOUTH, FabricParticipants.TO_INVENTORY.get().apply(south))
				                                  .put(Direction.WEST, FabricParticipants.TO_INVENTORY.get().apply(west))
				                                  .build(), true);
	}
}
