package io.github.astrarre.transfer.internal;

import io.github.astrarre.transfer.internal.inventory.CombinedSidedInventory;
import io.github.astrarre.transfer.v0.api.AstrarreParticipants;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.block.entity.BlockEntity;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.util.math.Direction;
import io.github.astrarre.v0.world.World;

import net.minecraft.inventory.SidedInventory;

public class TransferInternalAstrarre {
	@SuppressWarnings ({
			"unchecked",
			"rawtypes"
	})
	public static SidedInventory getSidedInventoryAt(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		BlockEntity entity = null;
		if (state.getBlock().hasBlockEntity()) {
			entity = world.getBlockEntity(pos);
		}
		return new CombinedSidedInventory(FabricParticipants.TO_INVENTORY.get().apply((Participant) AstrarreParticipants.ITEM_WORLD.get().get(
				Direction.UP,
				state,
				world,
				pos,
				entity)),
				FabricParticipants.TO_INVENTORY.get()
						.apply((Participant) AstrarreParticipants.ITEM_WORLD.get().get(Direction.DOWN, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get()
						.apply((Participant) AstrarreParticipants.ITEM_WORLD.get().get(Direction.NORTH, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get()
						.apply((Participant) AstrarreParticipants.ITEM_WORLD.get().get(Direction.SOUTH, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get()
						.apply((Participant) AstrarreParticipants.ITEM_WORLD.get().get(Direction.WEST, state, world, pos, entity)),
				FabricParticipants.TO_INVENTORY.get()
						.apply((Participant) AstrarreParticipants.ITEM_WORLD.get().get(Direction.EAST, state, world, pos, entity)));
	}
}
