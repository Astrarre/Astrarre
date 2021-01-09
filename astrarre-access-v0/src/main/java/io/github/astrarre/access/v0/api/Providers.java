package io.github.astrarre.access.v0.api;

import io.github.astrarre.access.internal.SidedInventoryAccess;
import io.github.astrarre.access.v0.api.func.EntityFunction;
import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.access.v0.api.util.BlockEntityProvider;
import io.github.astrarre.access.v0.api.util.BlockProvider;
import io.github.astrarre.access.v0.api.util.EntityProvider;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.inventory.Inventory;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.util.math.Direction;
import io.github.astrarre.v0.world.World;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.SidedInventory;

public class Providers {
	/**
	 * this is where you register any custom inventory getting implementations. <b>DO NOT GET INVENTORIES FROM THIS</b>, by default this includes:
	 *
	 * any Block that implements {@link io.github.astrarre.access.v0.api.util.BlockProvider} any BlockEntity that implements {@link
	 * io.github.astrarre.access.v0.api.util.BlockEntityProvider}
	 *
	 * @see #INVENTORY
	 */
	public static final RegistryProvider<WorldFunction<Inventory>, Inventory> INVENTORY_REGISTRY = new RegistryProvider<>(WorldFunction::andThen,
			WorldFunction.empty());

	/**
	 * this is where you register any custom inventory getting implementations. <b>DO NOT GET INVENTORIES FROM THIS</b>, by default this includes:
	 *
	 * any Entity that implements {@link io.github.astrarre.access.v0.api.util.EntityProvider}
	 * @see #INVENTORY
	 */
	public static final RegistryProvider<EntityFunction<Inventory>, Inventory> ENTITY_INVENTORY_REGISTRY = new RegistryProvider<>(EntityFunction::andThen,
			(direction, entity) -> null);
	/**
	 * this is where you get inventories, by default this includes:
	 *
	 * {@link #INVENTORY_REGISTRY} any Entity that implements Inventory directly any BlockEntity that implements Inventory directly
	 *
	 * @implNote HopperBlockEntity is mixed into to call INVENTORY_REGISTRY
	 */
	public static final WorldFunction.NoBlock<Inventory> INVENTORY = Providers::getInventoryAt;

	static {
		// world providers
		INVENTORY_REGISTRY.andThen(BlockProvider.getWorldFunction(INVENTORY_REGISTRY));
		INVENTORY_REGISTRY.andThen(BlockEntityProvider.getWorldFunction(INVENTORY_REGISTRY));

		// entity providers
		ENTITY_INVENTORY_REGISTRY.andThen(EntityProvider.getEntityFunction(ENTITY_INVENTORY_REGISTRY));

		/* this is replaced by a mixin into HopperBlockEntity#getInventoryAt, where they already call getEntities
		INVENTORY_REGISTRY.wraps(EntityFunction.of(ENTITY_INVENTORY_REGISTRY), func -> (direction, state, view, pos, entity) -> {
			for (Entity otherEntity : view.getOtherEntities(null, Box.newInstance(pos))) {
				Inventory inventory = func.get(direction, otherEntity);
				if (inventory != null) {
					return inventory;
				}
			}
			return null;
		});*/
	}

	public static Inventory getInventoryAt(Direction direction, World view, BlockPos pos) {
		net.minecraft.inventory.Inventory inventory = HopperBlockEntity.getInventoryAt((net.minecraft.world.World) view,
				(net.minecraft.util.math.BlockPos) pos);
		if (inventory instanceof SidedInventory) {
			return (Inventory) new SidedInventoryAccess((SidedInventory) inventory, (net.minecraft.util.math.Direction) (Object) direction);
		}
		return (Inventory) inventory;
	}
}
