package io.github.astrarre.testmod;

import java.util.Map;
import java.util.Objects;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.cache.CachedWorldQuery;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.transfer.internal.TransferInternalAstrarre;
import io.github.astrarre.transfer.internal.inventory.EmptyInventory;
import io.github.astrarre.transfer.v0.api.AstrarreParticipants;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import io.github.astrarre.v0.item.Item;
import io.github.astrarre.v0.item.Items;
import io.github.astrarre.v0.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TestModBlock extends Block implements BlockEntityProvider, InventoryProvider {
	public TestModBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new Entity();
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
		if (world instanceof World) {
			return TransferInternalAstrarre
					       .getSidedInventoryAt((io.github.astrarre.v0.world.World) world, (io.github.astrarre.v0.util.math.BlockPos) pos);
		}
		return EmptyInventory.INSTANCE;
	}

	public static class Entity extends BlockEntity
			implements io.github.astrarre.access.v0.api.provider.BlockEntityProvider, Participant<ItemKey>, Tickable {
		private final DiffKey.Map<Item, DiffKey.Map<NBTagView, Integer>> storage = new DiffKey.Map<>();
		private CachedWorldQuery query;
		private World world;
		private BlockPos pos;

		public Entity() {
			super(TestMod.BE_TYPE);
		}

		@Override
		public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
			this.storage.get(transaction).forEach((i, m) -> {
				for (Map.Entry<NBTagView, Integer> entry : m.get(transaction).entrySet()) {
					int current = entry.getValue();
					entry.setValue(current - insertable.insert(transaction, i.withTag(entry.getKey()), current));
				}
			});
		}

		@Override
		public int extract(@Nullable Transaction transaction, ItemKey type, int quantity) {
			DiffKey.Map<NBTagView, Integer> key = this.storage.get(transaction).get(type.asItem());
			if (key == null) {
				return 0;
			}

			Map<NBTagView, Integer> view = key.get(transaction);
			NBTagView nbt = type.getTag();
			int toExtract = Math.min(view.getOrDefault(nbt, 0), quantity);
			if (toExtract == 0) {
				return 0;
			}

			view.compute(nbt, (k, i) -> i - toExtract);
			return toExtract;
		}

		@Override
		public int insert(@Nullable Transaction transaction, ItemKey type, int quantity) {
			this.storage.get(transaction).computeIfAbsent(type.asItem(), t -> new DiffKey.Map<>()).get(transaction)
					.compute(type.getTag(), (k, i) -> (i == null ? 0 : i) + quantity);
			return quantity;
		}

		@Override
		public @Nullable Object get(Access<?> access, Direction direction) {
			if (AstrarreParticipants.ITEM_WORLD == access) {
				return this;
			}
			return null;
		}

		@Override
		public void tick() {
			World curr = this.getWorld();
			BlockPos pos = this.getPos();

			if (curr != this.world || Objects.equals(pos, this.pos)) {
				this.world = curr;
				this.pos = pos;
				this.query = CachedWorldQuery.getOrCreate((io.github.astrarre.v0.util.math.BlockPos) pos.add(0, -1, 0),
						(io.github.astrarre.v0.world.World) curr);
			}

			Participant<ItemKey> participant = this.query.get(AstrarreParticipants.ITEM_WORLD, Direction.UP);
			int creb = participant.extract(null, Items.STONE, 10);
			if (creb != 0) {
				this.insert(null, Items.STONE, creb);
				System.out.println(creb);
			}
		}
	}
}
