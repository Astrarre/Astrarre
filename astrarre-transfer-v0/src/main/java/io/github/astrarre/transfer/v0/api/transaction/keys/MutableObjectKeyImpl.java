package io.github.astrarre.transfer.v0.api.transaction.keys;

import java.util.function.UnaryOperator;

import io.github.astrarre.transfer.internal.CloneableRandom;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

public class MutableObjectKeyImpl<T> extends ObjectKeyImpl<T> {
	public static MutableObjectKeyImpl<CloneableRandom> random() {
		return new MutableObjectKeyImpl<>(new CloneableRandom(), CloneableRandom::clone);
	}

	public static MutableObjectKeyImpl<CloneableRandom> random(long initialSeed) {
		return new MutableObjectKeyImpl<>(new CloneableRandom(initialSeed), CloneableRandom::clone);
	}

	private final UnaryOperator<T> copier;

	public MutableObjectKeyImpl(T originalValue, UnaryOperator<T> copier) {
		super(originalValue);
		this.copier = copier;
	}

	public MutableObjectKeyImpl(UnaryOperator<T> copier) {
		this.copier = copier;
	}

	@Override
	protected T top(Transaction transaction) {
		if(!this.handler.isTop(transaction)) {
			T top = this.copier.apply(super.top(transaction));
			this.set(transaction, top);
			return top;
		}
		return super.top(transaction);
	}
}
