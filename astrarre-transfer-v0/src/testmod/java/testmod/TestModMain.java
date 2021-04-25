package testmod;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.fabric.provider.ItemProvider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.ad.AstrarreTransferTooltippedItem;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;
import io.github.astrarre.transfer.v0.api.player.PlayerParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;

public class TestModMain implements ModInitializer {
	public static final Block TANK_BLOCK = Registry.register(Registry.BLOCK, new Identifier("testmod:tank_block"), new MyTankBlock(AbstractBlock.Settings.copy(
			Blocks.STONE)));
	public static final BlockEntityType<?> TANK_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("testmod:tank_block"), BlockEntityType.Builder.create(MyTankBlock.Tile::new, TANK_BLOCK).build(null));

	public static final FluidCellItem ITEM = new FluidCellItem();

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fluid_cell"), ITEM);
		Registry.register(Registry.ITEM, new Identifier("test_item"), new MyItem(new Item.Settings()));
	}

	public static class MyItem extends AstrarreTransferTooltippedItem /*just Item works as well*/ {
		public MyItem(Settings settings) {
			super(settings);
		}

		@Override
		public ActionResult useOnBlock(ItemUsageContext context) {
			World world = context.getWorld();
			if (!world.isClient) {
				Participant<Fluid> participant = FabricParticipants.FLUID_WORLD.get().get(context.getSide(), world, context.getBlockPos());
				if (context.getPlayer().isSneaking()) {
					// a FixedObjectVolume is a container with a fixed size, so this container can hold 1 bottle's worth of fluid
					FixedObjectVolume<Fluid> container = new FixedObjectVolume(Fluids.EMPTY, Droplet.BOTTLE);
					// the participant will try to dump as much of it's contents into our container as possible
					participant.extract(Transaction.GLOBAL, container);
					context.getPlayer().sendMessage(new LiteralText("Extracted " + container.getQuantity(Transaction.GLOBAL) + "dp"), false);
				} else {
					int inserted = participant.insert(Transaction.GLOBAL, Fluids.WATER, Droplet.BUCKET);
					context.getPlayer().sendMessage(new LiteralText("Inserted " + inserted + "dp"), false);
				}
			}
			return ActionResult.CONSUME;
		}
	}

	public static class FluidCellItem extends Item implements ItemProvider {
		public FluidCellItem() {
			super(new Settings().maxCount(1));
		}

		@Override
		public @Nullable Object get(Access<?> access, Direction direction, ItemKey key, int count, Object container) {
			if (access == FabricParticipants.FLUID_ITEM) {
				ReplacingParticipant<ItemKey> participant = (ReplacingParticipant<ItemKey>) container;
				return new FluidCellParticipant(participant, key);
			}
			return null;
		}

		@Override
		public ActionResult useOnBlock(ItemUsageContext context) {
			World world = context.getWorld();
			if (!world.isClient) {
				ReplacingParticipant<ItemKey> participant = ((PlayerParticipant) FabricParticipants.FROM_INVENTORY.get().apply(null, context.getPlayer().inventory)).getHandReplacingParticipant(context.getHand());
				Participant<Fluid> fluidCell = FabricParticipants.FLUID_ITEM.get().get(null, ItemKey.of(context.getStack()), 1, participant);
				fluidCell.insert(Transaction.GLOBAL, Fluids.WATER, 100);
				System.out.println(fluidCell.insert(Transaction.GLOBAL, Fluids.LAVA, 100));
			}
			return ActionResult.CONSUME;
		}
	}

	public static class FluidCellParticipant implements Participant<Fluid> {
		public final ReplacingParticipant<ItemKey> context;
		public final Key.Object<ItemKey> current;

		public FluidCellParticipant(ReplacingParticipant<ItemKey> context, ItemKey current) {
			this.context = context;
			this.current = new ObjectKeyImpl<>(current);
		}

		@Override
		public void extract(@Nullable Transaction transaction, Insertable<Fluid> insertable) {
			ItemKey current = this.current.get(transaction);
			Fluid fluid = Registry.FLUID.get(new Identifier(current.getTag().getString("fluid")));
			if (fluid != Fluids.EMPTY) {
				int amount = current.getTag().getInt("amount");
				try (Transaction action = Transaction.create()) {
					int remainder = amount - insertable.insert(action, fluid, amount);
					ItemKey newKey;
					if (remainder == 0) {
						newKey = ItemKey.of(ITEM);
					} else {
						newKey = current.withTag(current.getTag().toBuilder().putInt("amount", remainder));
					}

					if (!this.context.replace(action, current, 1, newKey, 1)) {
						action.abort();
					}
				}
			}
		}

		@Override
		public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
			if (quantity == 0 || Fluids.EMPTY == type) {
				return 0;
			}
			ItemKey current = this.current.get(transaction);
			Fluid fluid = Registry.FLUID.get(new Identifier(current.getTag().getString("fluid")));
			if (fluid == Fluids.EMPTY || fluid == type) {
				int count = current.getTag().getInt("amount") + quantity;
				try (Transaction action = Transaction.create()) {
					ItemKey newKey = current.withTag(current.getTag().toBuilder().putInt("amount", count).putString("fluid", Registry.FLUID.getId(type).toString()));
					if (!this.context.replace(action, current, 1, newKey, 1)) {
						action.abort();
						return 0;
					} else {
						return quantity;
					}
				}
			}
			return 0;
		}
	}
}
