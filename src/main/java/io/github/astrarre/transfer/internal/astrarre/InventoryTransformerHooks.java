package io.github.astrarre.transfer.internal.astrarre;

import java.util.Collections;
import java.util.function.Supplier;

import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.internal.fabric.inventory.CombinedSidedInventory;
import io.github.astrarre.transfer.v0.api.AstrarreParticipants;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.v0.util.math.Direction;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings ({
		"rawtypes",
		"unchecked"
})
public class InventoryTransformerHooks {
	private static final Supplier<WorldFunction<Participant<ItemKey>>> FUNCTION = AstrarreParticipants.ITEM_WORLD.getExcluding(Collections.singleton(TransferInternalAstrarre.HOPPER_BLOCK_ENTITY_GET_INVENTORY));

	public static Inventory get(BlockPos pos, World world, BlockState state) {
		if(!state.getBlock().hasBlockEntity()) {
			return get(pos, world, state, null);
		}
		return null;
	}

	public static Inventory get(BlockPos posM, World worldM, BlockState stateM, BlockEntity entityM) {
		io.github.astrarre.v0.block.BlockState state = (io.github.astrarre.v0.block.BlockState) stateM;
		io.github.astrarre.v0.world.World world = (io.github.astrarre.v0.world.World) worldM;
		io.github.astrarre.v0.util.math.BlockPos pos = (io.github.astrarre.v0.util.math.BlockPos) posM;
		io.github.astrarre.v0.block.entity.BlockEntity entity = (io.github.astrarre.v0.block.entity.BlockEntity) entityM;

		WorldFunction<Participant<ItemKey>> function = FUNCTION.get();
		Participant up = function.get(Direction.UP, state, world, pos, entity),
				down = function.get(Direction.DOWN, state, world, pos, entity),
				north = function.get(Direction.NORTH, state, world, pos, entity),
				south = function.get(Direction.SOUTH, state, world, pos, entity),
				west = function.get(Direction.WEST, state, world, pos, entity),
				east = function.get(Direction.EAST, state, world, pos, entity);

		if(up == Participants.EMPTY && down == Participants.EMPTY && north == Participants.EMPTY && south == Participants.EMPTY && west == Participants.EMPTY && east == Participants.EMPTY) {
			return null;
		}

		return new CombinedSidedInventory(
				FabricParticipants.TO_INVENTORY.get().apply(up),
				FabricParticipants.TO_INVENTORY.get().apply(down),
				FabricParticipants.TO_INVENTORY.get().apply(north),
				FabricParticipants.TO_INVENTORY.get().apply(south),
				FabricParticipants.TO_INVENTORY.get().apply(west),
				FabricParticipants.TO_INVENTORY.get().apply(east));
	}
}
