package io.github.astrarre.transfer.internal.compat;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.mixin.BucketItemAccess_AccessFluid;
import io.github.astrarre.transfer.v0.api.Droplet;
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
		ItemKey current = this.currentKey.get(transaction);
		Fluid fluid = this.get(current);
		if (Fluids.EMPTY != fluid) {
			try(Transaction action = Transaction.create()) {
				int toInsert = Droplet.minMultiply(this.quantity.get(action), this.quantity(current));
				int insert = insertable.insert(action, fluid, toInsert);
				if(insert != toInsert) { // too lazy to do partial results, if your stacking filled buckets it's your fault
					action.abort();
					return;
				}

				int quantity = this.quantity.get(action);
				// todo if player context use empty bucket
				if(this.extractTest(action, this.currentKey.get(action), quantity)) {
					this.currentKey.set(action, this.emptyItem());
				} else {
					action.abort();
				}
			}
		}
	}

	protected boolean extractTest(Transaction transaction, ItemKey current, int quantity) {
		return this.container.replace(transaction, current, quantity, this.emptyItem(), quantity);
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
		if(quantity == 0 || type == Fluids.EMPTY || !this.isValid(type)) return 0;
		Fluid fluid = this.get(this.currentKey.get(transaction));
		if(fluid == Fluids.EMPTY) {
			ItemKey current = this.currentKey.get(transaction);
			int neededBuckets = Math.floorDiv(quantity, this.quantity(current));
			int currentBuckets = this.quantity.get(transaction);
			int bucketsToTake = neededBuckets;
			if(neededBuckets > currentBuckets) {
				bucketsToTake = currentBuckets;
			}
			ItemKey filledItem = this.filledItem(type);
			if(this.container.replace(transaction, current, bucketsToTake, filledItem, bucketsToTake)) {
				if(bucketsToTake == currentBuckets) { // if every bucket was taken out
					this.currentKey.set(transaction, filledItem);
					this.quantity.set(transaction, filledItem.getMaxStackSize());
				} else {
					this.quantity.set(transaction, currentBuckets - bucketsToTake);
				}
				return neededBuckets * Droplet.BUCKET;
			}
		}
		return 0;
	}

	protected boolean isValid(Fluid fluid) {
		return true;
	}

	protected ItemKey filledItem(Fluid fluid) {
		return ItemKey.of(fluid.getBucketItem());
	}

	protected ItemKey emptyItem() {
		return ItemKey.of(Items.BUCKET);
	}

	protected Fluid get(ItemKey item) {
		return ((BucketItemAccess_AccessFluid)item.getItem()).getFluid();
	}

	protected int quantity(ItemKey key) {
		return Droplet.BUCKET;
	}
}
