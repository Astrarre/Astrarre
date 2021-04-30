package io.github.astrarre.gui.v0.fabric.adapter.slot;

import java.util.Objects;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * a slot who's inventory is retrieved from HopperBlockEntity#getInventoryAt.
 *
 */
public class AWorldInventorySlot extends ASlot {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry
			                                                    .register(Id.create("astrarre-gui-v0", "world_slot"), AWorldInventorySlot::new);
	private World world;
	private BlockPos pos;

	protected AWorldInventorySlot(World world, BlockPos pos, int index) {
		this(ENTRY, world, pos, null, index);
	}

	protected AWorldInventorySlot(DrawableRegistry.Entry id, World world, BlockPos pos, @Nullable Inventory defaultInventory, int index) {
		super(id, defaultInventory, index);
		if (defaultInventory == null) {
			this.inventory = this.getInventory(world, pos);
		}
		this.world = world;
		this.pos = pos;
	}

	protected Inventory getInventory(World world, BlockPos pos) {
		return HopperBlockEntity.getInventoryAt(this.world, this.pos);
	}

	@Environment (EnvType.CLIENT)
	private AWorldInventorySlot(NBTagView input) {
		this(ENTRY, input);
	}

	@Environment (EnvType.CLIENT)
	protected AWorldInventorySlot(DrawableRegistry.Entry id, NBTagView input) {
		super(id, input);
	}

	public static void init() {
	}

	@Override
	protected void writeInventoryData(NBTagView.Builder output, Inventory inventory) {
		Serializer.ID.save(output, "world", Id.of(this.getWorld().getRegistryKey().getValue()));
		FabricSerializers.BLOCK_POS.save(output, "pos", this.getPos());
	}

	@Override
	@Environment (EnvType.CLIENT)
	protected Inventory readInventoryData(NBTagView input) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && Objects.equals(client.world.getRegistryKey().getValue(), Serializer.ID.read(input, "world"))) {
			this.world = client.world;
			this.pos = FabricSerializers.BLOCK_POS.read(input, "pos");
			return this.getInventory(this.world, this.pos);
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
