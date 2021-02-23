package io.github.astrarre.transfer.internal;

import io.github.astrarre.access.v0.api.WorldAccess;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.internal.inventory.CombinedSidedInventory;
import io.github.astrarre.transfer.v0.api.AstrarreParticipants;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.block.entity.BlockEntity;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.util.math.Direction;
import io.github.astrarre.v0.world.World;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;

public class TransferInternalAstrarre {
	/**
	 * get participants from HopperEntity#getInventoryAt, this has to be a seperate registry to allow bidirectional compat
	 */
	public static final WorldAccess<Participant<ItemKey>> FROM_INVENTORY = new WorldAccess<>(Participants.EMPTY.cast());

	// used for HopperEntity stuff
	public static final ThreadLocal<net.minecraft.block.BlockState> CACHED_STATE = new ThreadLocal<>();
	public static final ThreadLocal<net.minecraft.block.entity.BlockEntity> CACHED_BLOCK_ENTITY = new ThreadLocal<>();

	static {
		FROM_INVENTORY.andThen((direction, state, view, pos, entity) -> {
			CACHED_STATE.set((net.minecraft.block.BlockState) state);
			CACHED_BLOCK_ENTITY.set((net.minecraft.block.entity.BlockEntity) entity);
			Inventory inventory = HopperBlockEntity.getInventoryAt((net.minecraft.world.World) view, (net.minecraft.util.math.BlockPos) pos);
			CACHED_STATE.set(null);
			CACHED_BLOCK_ENTITY.set(null);
			if(inventory == null) return null;
			return (Participant) FabricParticipants.FROM_INVENTORY.get().apply((net.minecraft.util.math.Direction) (Object) direction, inventory);
		});
	}

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
