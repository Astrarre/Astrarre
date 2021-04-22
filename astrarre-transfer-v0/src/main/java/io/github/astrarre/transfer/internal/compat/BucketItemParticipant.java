package io.github.astrarre.transfer.internal.compat;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.mixin.BucketItemAccess;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;

public class BucketItemParticipant implements Participant<Fluid> {
	public final Key.Object<ItemKey> currentKey;
	public final Participant<ItemKey> container;

	public BucketItemParticipant(ItemKey key, Participant<ItemKey> container) {
		this.currentKey = new ObjectKeyImpl<>(key);
		this.container = container;
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<Fluid> insertable) {
		BucketItemAccess access = (BucketItemAccess) this.currentKey.get(transaction).getItem();
		Fluid fluid = access.getFluid();
		if (Fluids.EMPTY != fluid) {
			try(Transaction action = Transaction.create()) {
				int insert = insertable.insert(transaction, access.getFluid(), Droplet.BUCKET);
				if(insert == Droplet.BUCKET) {
					action.abort();
				}
				// todo with player context BucketItem#getEmptiedStack
				this.container.extract(action, this.currentKey.get(action), 1);

			}
		}
	}

	@Override
	public int extract(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
		return 0;
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
		return 0;
	}
}
