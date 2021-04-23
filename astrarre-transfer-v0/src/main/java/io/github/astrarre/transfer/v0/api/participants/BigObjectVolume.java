package io.github.astrarre.transfer.v0.api.participants;

import java.math.BigInteger;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an object volume that can store an unlimited* amount of a resource
 */
public class BigObjectVolume<T> implements Participant<T> {
	public static final BigInteger INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
	protected final T empty;
	protected final Key.Object<T> type;
	protected final Key.Object<BigInteger> quantity;

	public BigObjectVolume(T empty) {
		this(empty, empty, BigInteger.ZERO);
	}

	public BigObjectVolume(T empty, T object, BigInteger quantity) {
		this.empty = empty;
		if (object == this.empty && !BigInteger.ZERO.equals(quantity)) {
			throw new IllegalArgumentException("cannot have " + quantity + " units of EMPTY!");
		} else if (quantity.signum() == -1) {
			throw new IllegalArgumentException("Cannot have negative units of " + object);
		}

		if (BigInteger.ZERO.equals(quantity)) {
			object = empty;
		}

		this.type = new ObjectKeyImpl<>(object);
		this.quantity = new ObjectKeyImpl<>(quantity);
	}

	/**
	 * @return the resource instance
	 */
	public T getType(@Nullable Transaction transaction) {
		return this.type.get(transaction);
	}

	/**
	 * @return the amount stored
	 */
	public BigInteger getQuantity(@Nullable Transaction transaction) {
		return this.quantity.get(transaction);
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		if (quantity == 0) {
			return 0;
		}

		T fluid = this.type.get(transaction);
		if (fluid == this.empty || fluid == type) {
			if (fluid != type) {
				this.type.set(transaction, type);
			}

			BigInteger current = this.quantity.get(transaction);
			this.quantity.set(transaction, current.add(BigInteger.valueOf(quantity)));
			return quantity;
		}
		return 0;
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<T> insertable) {
		BigInteger oldLevel = this.quantity.get(transaction);
		int amount = insertable.insert(transaction, this.type.get(transaction), INT_MAX.min(oldLevel).intValue());
		BigInteger newLevel = oldLevel.subtract(BigInteger.valueOf(amount));
		this.quantity.set(transaction, newLevel);
		if (BigInteger.ZERO.equals(newLevel)) {
			this.type.set(transaction, this.empty);
		}
	}

	@Override
	public int extract(@Nullable Transaction transaction, @NotNull T type, int quantity) {
		if (quantity == 0) {
			return 0;
		}

		if (this.type.get(transaction) == type) {
			BigInteger oldLevel = this.quantity.get(transaction);
			BigInteger toExtract = BigInteger.valueOf(quantity).min(oldLevel);
			BigInteger newLevel = oldLevel.subtract(toExtract);
			if (BigInteger.ZERO.equals(newLevel)) {
				this.type.set(transaction, this.empty);
			}
			return toExtract.intValueExact();
		}

		return 0;
	}

	@Override
	public boolean isEmpty(@Nullable Transaction transaction) {
		return this.type.get(transaction) == this.empty;
	}

	@Override
	public void clear(@Nullable Transaction transaction) {
		this.type.set(transaction, this.empty);
		this.quantity.set(transaction, BigInteger.ZERO);
	}
}
