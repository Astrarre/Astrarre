package io.github.astrarre.gui.v0.fabric.adapter.slot;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * AWorldInventory slot, but for block entities specifically
 */
public class ABlockEntityInventorySlot<B extends BlockEntity & Inventory> extends AWorldInventorySlot {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(
			Id.create("astrarre-gui-v0", "block_entity_slot"),
			ABlockEntityInventorySlot::new);
	protected final B entity;

	public ABlockEntityInventorySlot(B entity, int index) {
		super(ENTRY, null, null, entity, index);
		this.entity = entity;
	}

	protected ABlockEntityInventorySlot(DrawableRegistry.Entry id, B entity, int index) {
		super(id, null, null, entity, index);
		this.entity = entity;
	}

	protected ABlockEntityInventorySlot(DrawableRegistry.Entry id, NBTagView input) {
		super(id, input);
		this.entity = null;
	}

	@Override
	protected Inventory getInventory(World world, BlockPos pos) {
		if (this.entity != null) {
			return this.entity;
		}
		BlockEntity entity = world.getBlockEntity(pos);
		return (Inventory) entity;
	}

	@Override
	public World getWorld() {
		return this.entity == null ? super.getWorld() : this.entity.getWorld();
	}

	@Override
	public BlockPos getPos() {
		return this.entity == null ? super.getPos() : this.entity.getPos();
	}

	public static void init() {}

	@Override
	public String toString() {
		return "BlockEntityInventorySlot at " + this.getPos() + " in " + this.getWorld().getRegistryKey().getValue();
	}
}
