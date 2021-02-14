package io.github.astrarre.transfer.v0.api.util;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.v0.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Participants implements Participant<Object> {
	/**
	 * a participant that cannot accept or give any resources
	 */
	EMPTY(true, true),

	/**
	 * a participant that voids anything it is given
	 */
	VOIDING(true, false) {
		@Override
		public int insert(Transaction transaction, Object type, int quantity) {
			return quantity;
		}
	},

	/**
	 * a participant that can have any *specific* resource extracted from it (does not work with wildcard extraction)
	 */
	CREATIVE(false, true) {
		@Override
		public int extract(Transaction transaction, Object type, int quantity) {
			return quantity;
		}
	},

	/**
	 * basically a combination of CREATIVE and VOIDING
	 */
	CREATIVE_SINK(false, false) {
		@Override
		public int extract(Transaction transaction, Object type, int quantity) {
			return quantity;
		}

		@Override
		public int insert(Transaction transaction, Object type, int quantity) {
			return quantity;
		}
	};

	/**
	 * if a participant is looking for a limited set of items, this can help narrow it down
	 */
	public static final FunctionAccess<Participant<ItemKey>, @NotNull Set<Item>> FILTERS = new FunctionAccess<Participant<ItemKey>, Set<Item>>((function,
			function2) -> participant -> Sets.union(
			function.apply(participant),
			function2.apply(participant)), p -> Collections.emptySet());

	private final boolean empty, full;

	Participants(boolean empty, boolean full) {
		this.empty = empty;
		this.full = full;
	}

	// @formatter:off
	@Override public void extract(Transaction transaction, Insertable<Object> insertable) {}
	@Override public int extract(Transaction transaction, Object type, int quantity) {return 0;}
	@Override public boolean isEmpty(@Nullable Transaction transaction) { return this.empty; }
	@Override public int insert(Transaction transaction, Object type, int quantity) {return 0;}
	@Override public boolean isFull(@Nullable Transaction transaction) { return this.full; }
	@Override public long getVersion() { return 0; }
	@Override
	public boolean supportsExtraction() {
		return this == CREATIVE || this == CREATIVE_SINK;
	}
	@Override
	public boolean supportsInsertion() {
		return this == VOIDING || this == CREATIVE_SINK;
	}
	// @formatter:on

	public <T> Participant<T> cast() {
		return (Participant<T>) this;
	}
}
