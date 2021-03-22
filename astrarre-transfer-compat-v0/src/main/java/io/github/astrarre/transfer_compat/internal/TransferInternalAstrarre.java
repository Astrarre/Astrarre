package io.github.astrarre.transfer_compat.internal;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;

public class TransferInternalAstrarre implements ModInitializer {
	private static final Method METHOD;
	static {
		Method target = null;
		for (Method method : HopperBlockEntity.class.getDeclaredMethods()) {
			if(method.getName().equals("astrarre_copied_getInventoryAt")) {
				target = method;
				break;
			}
		}
		if(target == null) {
			throw new IllegalStateException("Unable to find target method!");
		}
		METHOD = target;
	}

	private static Inventory getInvAt(World world, BlockPos pos) {
		try {
			return (Inventory) METHOD.invoke(world, pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .5D);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw Validate.rethrow(e);
		}
	}

	public static final WorldAccess<Participant<ItemKey>> HOPPER_BLOCK_ENTITY_GET_INVENTORY = new WorldAccess<>(Participants.EMPTY.cast());
	static {
		HOPPER_BLOCK_ENTITY_GET_INVENTORY.andThen((WorldFunction.NoBlock<Participant<ItemKey>>)(direction, world, pos) -> FabricParticipants.FROM_INVENTORY.get().apply(direction, getInvAt(world, pos)));
	}


	@Override
	public void onInitialize() {
		FabricParticipants.ITEM_WORLD.dependsOn(HOPPER_BLOCK_ENTITY_GET_INVENTORY);
	}
}
