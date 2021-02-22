package io.github.astrarre.testmod;

import java.util.HashMap;
import java.util.Map;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.Returns;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.item.nbt.NBTagView;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.util.Participants;
import io.github.astrarre.v0.item.Item;
import io.github.astrarre.v0.util.math.Direction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class TestModBlock extends Block implements BlockEntityProvider {
	public TestModBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new Entity();
	}

	public static class Entity extends BlockEntity implements io.github.astrarre.access.v0.fabric.provider.BlockEntityProvider, Participant<ItemKey> {
		private final Map<Item, Object2IntMap<NBTagView>> storage = new HashMap<>();

		public Entity() {
			super(TestMod.BE_TYPE);
		}

		@Override
		public <T> @Nullable T get(Access<? extends Returns<T>, T> access, Direction direction) {
			if(Participants.ITEM_WORLD == access) {
				return (T) this;
			} else if(Participants.FILTERS == access) {
				return (T) this.storage.keySet();
			}
			return null;
		}

		@Override
		public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
			this.storage.forEach((i, m) -> m.forEach((n, c) -> insertable.insert(transaction, i.withTag(n), c)));
		}

		@Override
		public int extract(@Nullable Transaction transaction, ItemKey type, int quantity) {
			Object2IntMap<NBTagView> view = this.storage.getOrDefault(type.asItem(), Object2IntMaps.emptyMap());
			NBTagView view1 = type.getTag();
			int toExtract = Math.min(view.getOrDefault(view1, 0), quantity);
			if (toExtract == 0) {
				return 0;
			}

			view.computeInt(view1, (k, i) -> i - quantity);
			return toExtract;
		}

		@Override
		public int insert(@Nullable Transaction transaction, ItemKey type, int quantity) {
			this.storage.getOrDefault(type.asItem(), Object2IntMaps.emptyMap()).computeInt(type.getTag(), (k, i) -> (i == null ? 0 : i) + quantity);
			return quantity;
		}
	}
}
