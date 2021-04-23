package io.github.astrarre.transfer.v0.api.participants;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.Nullable;

/**
 * a volume with unlimited* (int_max) storage capacity
 */
public class ObjectVolume<T> implements Slot<T> {
	public static final Serializer<ObjectVolume<ItemKey>> ITEM_KEY_SERIALIZER = serializer(ItemKey.EMPTY, ItemKey.SERIALIZER);
	protected final T empty;
	protected final Key.Object<T> type;
	protected final Key.Int quantity;
	public ObjectVolume(T empty) {
		this(empty, empty, 0);
	}

	public ObjectVolume(T empty, T object, int quantity) {
		this.empty = empty;
		if (object == empty && quantity != 0) {
			throw new IllegalArgumentException("cannot have " + quantity + " units of EMPTY!");
		} else if (quantity < 0) {
			throw new IllegalArgumentException("Cannot have negative units of " + object);
		}

		if (quantity == 0) {
			object = empty;
		}

		this.type = new ObjectKeyImpl<>(object);
		this.quantity = new IntKeyImpl(quantity);
	}

	/**
	 * @see FabricParticipants#FLUID_OBJECT_VOLUME_SERIALIZER
	 */
	public static <T> Serializer<ObjectVolume<T>> serializer(T empty, Serializer<T> typeSerializer) {
		return Serializer.of((tag) -> {
			NBTagView volume = tag.asTag();
			int quantity = volume.getInt("quantity");
			T object = typeSerializer.read(volume, "object");
			return new ObjectVolume<>(empty, object, quantity);
		}, t -> {
			NBTagView.Builder volume = NBTagView.builder().putInt("quantity", t.quantity.get(Transaction.GLOBAL));
			typeSerializer.save(volume, "object", t.type.get(Transaction.GLOBAL));
			return volume;
		});
	}

	@Override
	public T getKey(@Nullable Transaction transaction) {
		return this.type.get(transaction);
	}

	@Override
	public int getQuantity(@Nullable Transaction transaction) {
		return this.quantity.get(transaction);
	}

	@Override
	public boolean set(@Nullable Transaction transaction, T key, int quantity) {
		if (quantity == 0) {
			key = this.empty;
		}
		this.type.set(transaction, key);
		this.quantity.set(transaction, quantity);
		return true;
	}

	@Override
	public void clear(@Nullable Transaction transaction) {
		this.type.set(transaction, this.empty);
		this.quantity.set(transaction, 0);
	}
}
