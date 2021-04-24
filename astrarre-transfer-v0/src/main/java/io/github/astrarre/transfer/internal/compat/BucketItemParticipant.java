package io.github.astrarre.transfer.internal.compat;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.mixin.BucketItemAccess;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;

public class BucketItemParticipant implements Participant<Fluid> {
	public final Key.Object<ItemKey> currentKey;
	public final Key.Int quantity;
	public final ReplacingParticipant<ItemKey> container;

	public BucketItemParticipant(ItemKey key, int quantity, ReplacingParticipant<ItemKey> container) {
		this.currentKey = new ObjectKeyImpl<>(key);
		this.quantity = new IntKeyImpl(quantity);
		this.container = container;
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<Fluid> insertable) {
		BucketItemAccess access = (BucketItemAccess) this.currentKey.get(transaction).getItem();
		Fluid fluid = access.getFluid();
		if (Fluids.EMPTY != fluid) {
			try(Transaction action = Transaction.create()) {
				int toInsert = Droplet.minMultiply(this.quantity.get(action), Droplet.BUCKET);
				int insert = insertable.insert(action, access.getFluid(), toInsert);
				if(insert != toInsert) { // too lazy to do partial results, if your stacking filled buckets it's your fault
					action.abort();
				}

				int quantity = this.quantity.get(action);
				// todo if player context use empty bucket
				if(this.container.replace(action, this.currentKey.get(action), quantity, ItemKey.of(Items.BUCKET), quantity)) {
					this.currentKey.set(action, ItemKey.of(Items.BUCKET));
				} else {
					action.abort();
				}
			}
		}
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
		if(quantity == 0 || type == Fluids.EMPTY) return 0;
		BucketItemAccess access = (BucketItemAccess) this.currentKey.get(transaction).getItem();
		Fluid fluid = access.getFluid();
		if(fluid == Fluids.EMPTY) {
			ItemKey current = this.currentKey.get(transaction);
			int neededBuckets = Math.floorDiv(quantity, Droplet.BUCKET);
			int currentBuckets = this.quantity.get(transaction);
			int bucketsToTake = neededBuckets;
			if(neededBuckets > currentBuckets) {
				bucketsToTake = currentBuckets;
			}
			if(this.container.replace(transaction, current, bucketsToTake, ItemKey.of(type.getBucketItem()), bucketsToTake)) {
				if(bucketsToTake == currentBuckets) { // if every bucket was taken out
					this.currentKey.set(transaction, ItemKey.of(type.getBucketItem()));
					// we can make this assumption because our api is actually good, so even in the worst case scenario there wont be duplication/loss
					this.quantity.set(transaction, type.getBucketItem().getMaxCount());
				} else {
					this.quantity.set(transaction, currentBuckets - bucketsToTake);
				}
				return neededBuckets * Droplet.BUCKET;
			}
		}
		return 0;
	}
}
