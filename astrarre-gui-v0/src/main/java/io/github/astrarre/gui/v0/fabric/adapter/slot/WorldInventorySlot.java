package io.github.astrarre.gui.v0.fabric.adapter.slot;

import java.util.Objects;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.fabric.FabricData;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * a slot who's inventory is retrieved from HopperBlockEntity#getInventoryAt
 */
public class WorldInventorySlot extends Slot {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "world_slot"), WorldInventorySlot::new);
	private World world;
	private BlockPos pos;
	protected WorldInventorySlot(World world, BlockPos pos, int index) {
		this(ENTRY, world, pos, index);
	}

	protected WorldInventorySlot(DrawableRegistry.Entry id, World world, BlockPos pos, int index) {
		super(id, HopperBlockEntity.getInventoryAt(world, pos), index);
		this.world = world;
		this.pos = pos;
	}

	@Environment(EnvType.CLIENT)
	private WorldInventorySlot(Input input) {
		this(ENTRY, input);
	}

	@Environment(EnvType.CLIENT)
	protected WorldInventorySlot(DrawableRegistry.Entry id, Input input) {
		super(id, input);
	}

	public static void init() {
	}

	@Override
	protected void writeInventoryData(Output output, Inventory inventory) {
		output.writeId(Id.of(this.world.getRegistryKey().getValue()));
		FabricData.from(output).writeBlockPos(this.pos);
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected Inventory readInventoryData(Input input) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.world != null && Objects.equals(client.world.getRegistryKey().getValue(), input.readId().to())) {
			this.world = client.world;
			this.pos = FabricData.readPos(input);
			return HopperBlockEntity.getInventoryAt(this.world, this.pos);
		}
		return null;
	}

	public World getWorld() {
		return this.world;
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
