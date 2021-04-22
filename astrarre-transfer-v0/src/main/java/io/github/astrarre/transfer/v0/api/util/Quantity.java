package io.github.astrarre.transfer.v0.api.util;

import java.util.Objects;

import io.github.astrarre.itemview.v0.fabric.ItemKey;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;

public final class Quantity<T> {
	public static final Quantity<Fluid> EMPTY_FLUID = new Quantity<>(Fluids.EMPTY, 0);
	public final T type;
	public final int amount;

	public static Quantity<ItemKey> of(ItemStack stack) {
		return new Quantity<>(ItemKey.of(stack), stack.getCount());
	}

	public static <T> Quantity<T> of(T fluid, int amount) {
		return new Quantity<>(fluid, amount);
	}

	public Quantity(T type, int amount) {
		this.type = type;
		this.amount = amount;
	}

	public Quantity<T> withAmount(int amount) {
		return new Quantity<>(this.type, amount);
	}

	public Quantity<T> withType(T t) {
		return new Quantity<>(t, this.amount);
	}

	public <A> Quantity<A> withOtherType(A a) {
		return new Quantity<>(a, this.amount);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Quantity)) {
			return false;
		}

		Quantity<?> quantity = (Quantity<?>) o;

		if (this.amount != quantity.amount) {
			return false;
		}
		return Objects.equals(this.type, quantity.type);
	}

	@Override
	public int hashCode() {
		int result = this.type != null ? this.type.hashCode() : 0;
		result = 31 * result + this.amount;
		return result;
	}

	@Override
	public String toString() {
		return this.type + " x" + this.amount;
	}

	public boolean isEmpty() {
		return this.amount == 0;
	}

	public boolean isTypeEqual(T type) {
		return this.type.equals(type);
	}
}
