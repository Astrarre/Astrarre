package io.github.astrarre.transfer.v0.api.participants;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.transfer.v0.fabric.participants.item.ItemSlotParticipant;

/**
 * an object volume that has a maximum quantity.
 * @see ItemSlotParticipant
 */
public class FixedObjectVolume<T> extends ObjectVolume<T> {
	public static final Serializer<FixedObjectVolume<ItemKey>> ITEM_KEY_SERIALIZER = fixedSerializer(ItemKey.EMPTY, ItemKey.SERIALIZER);
	protected int max;

	/**
	 * @see FabricParticipants#FLUID_FIXED_OBJECT_VOLUME_SERIALIZER
	 */
	public static <T> Serializer<FixedObjectVolume<T>> fixedSerializer(T empty, Serializer<T> typeSerializer) {
		return Serializer.of((tag, s) -> {
			NBTagView volume = tag.getTag(s);
			int quantity = volume.getInt("quantity");
			int max = volume.getInt("max");
			T object = typeSerializer.read(volume, "object");
			return new FixedObjectVolume<>(empty, object, quantity, max);
		}, (tag, s, t) -> {
			NBTagView.Builder volume = NBTagView.builder().putInt(s, t.quantity.get(Transaction.GLOBAL)).putInt(s, t.getMax(Transaction.GLOBAL));
			typeSerializer.save(volume, "object", t.type.get(Transaction.GLOBAL));
			tag.putTag(s, volume);
		});
	}

	/**
	 * @param empty the 'empty' version of the object (eg. Fluid#EMPTY)
	 */
	public FixedObjectVolume(T empty, int max) {
		super(empty);
		this.max = max;
	}

	public FixedObjectVolume(T empty, T object, int quantity, int max) {
		super(empty, object, quantity);
		this.max = max;
	}


	@Override
	public int insert(Transaction transaction, T type, int quantity) {
		int currentCount = this.quantity.get(transaction);
		return super.insert(transaction, type, Math.min(this.getMax(transaction) - currentCount, Math.min(this.getMaxStackSize(type) - currentCount, quantity)));
	}

	/**
	 * @return if the maximum size of the container is dynamic, this can be overriden
	 */
	public int getMax(Transaction transaction) {
		return this.max;
	}

	public int getMaxStackSize(T type) {
		return Integer.MAX_VALUE;
	}
}
