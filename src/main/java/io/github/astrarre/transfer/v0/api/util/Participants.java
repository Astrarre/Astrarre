package io.github.astrarre.transfer.v0.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.access.v0.api.func.AccessFunction;
import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import io.github.astrarre.transfer.internal.NUtil;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.AggregateParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.v0.fluid.Fluid;
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
	public static final FunctionAccess<Insertable<TaggedItem>, @NotNull Set<Item>> FILTERS = FunctionAccess.newInstance(sets -> {
		Set<Item> combined = new HashSet<>();
		sets.forEach(combined::addAll);
		return combined;
	});

	/**
	 * This can be used to extract fluids from items, the participant is where items are dumped or extracted from. For example, a water bucket will
	 * try to insert empty buckets into the participant if it is emptied.
	 *
	 * todo just go with the direct access instead of trying to heuristically find the items
	 */
	public static final FunctionAccess<Participant<TaggedItem>, Participant<Fluid>> ITEM_FLUID =
			FunctionAccess.newInstance(AggregateParticipant::merge);

	/**
	 * This is a central registry for registering wrapper containers. If your container simply wraps another container (to add filtering) then be
	 * sure
	 * to register it here. Keep in mind this will not recursively find wrappers!
	 *
	 * <b>IF YOUR ACCESS USES INSERTABLE/EXTRACTABLE, USE THOSE WRAPPER ACCESSES</b>
	 *
	 * @implNote The person writing the delegate class does not know of your custom api interface, therefore <b>YOU</b> must handle compatibility.
	 * 		Additionally if someone wraps a container, its behavior wont be the same as its delegates (obviously, why else would they wrap it), so
	 * 		you
	 * 		should account for this in your compat layer
	 * @see #unwrap(Access, Object)
	 */
	public static final FunctionAccess<Participant<?>, Iterable<Participant<?>>> AGGREGATE_WRAPPERS = new FunctionAccess<>();

	/**
	 * (a circular dependency with {@link #AGGREGATE_WRAPPERS} that checks for wrapper with only one participant
	 *
	 * if your participant has a single delegate, use this registry. if you can only add compatibility for participants with one delegate, but not
	 * multiple delegates, use this registry.
	 *
	 * @see #unwrap(Access, Object)
	 */
	public static final FunctionAccess<Participant<?>, Participant<?>> DIRECT_WRAPPERS = new FunctionAccess<>();

	public static final FunctionAccess<Insertable<?>, Iterable<Insertable<?>>> AGGREGATE_WRAPPERS_INSERTABLE = new FunctionAccess<>();
	public static final FunctionAccess<Insertable<?>, Insertable<?>> DIRECT_WRAPPERS_INSERTABLE = new FunctionAccess<>();

	public static final FunctionAccess<Extractable<?>, Iterable<Extractable<?>>> AGGREGATE_WRAPPERS_EXTRACTABLE = new FunctionAccess<>();
	public static final FunctionAccess<Extractable<?>, Extractable<?>> DIRECT_WRAPPERS_EXTRACTABLE = new FunctionAccess<>();
	public static final WorldAccess<Participant<TaggedItem>> ITEM_WORLD = new WorldAccess<>();
	public static final WorldAccess<Participant<Fluid>> FLUID_WORLD = new WorldAccess<>();


	static {
		FILTERS.dependsOn(AGGREGATE_WRAPPERS_INSERTABLE, function -> insertable -> {
			Collection<Insertable<TaggedItem>> wrapped = Participants.unwrapInternal((AccessFunction) function, insertable);
			if (wrapped == null) {
				return Collections.emptySet();
			}

			Set<Item> combined = null;
			for (Insertable<TaggedItem> delegate : wrapped) {
				combined = NUtil.addAll(combined, FILTERS.get().apply(delegate));
			}

			return NUtil.nn(combined);
		});

		ITEM_WORLD.addWorldProviderFunctions();
		FLUID_WORLD.addWorldProviderFunctions();
		AGGREGATE_WRAPPERS.addProviderFunction();
		DIRECT_WRAPPERS.addProviderFunction();
		AGGREGATE_WRAPPERS_INSERTABLE.addProviderFunction();
		DIRECT_WRAPPERS_INSERTABLE.addProviderFunction();
		AGGREGATE_WRAPPERS_EXTRACTABLE.addProviderFunction();
		DIRECT_WRAPPERS_EXTRACTABLE.addProviderFunction();
		ITEM_FLUID.addProviderFunction();
		FILTERS.addProviderFunction();

		AGGREGATE_WRAPPERS.dependsOn(DIRECT_WRAPPERS, p -> a -> Collections.singleton(p.apply(a)));
		DIRECT_WRAPPERS.dependsOn(AGGREGATE_WRAPPERS, p -> a -> getOnly(p.apply(a)));
		AGGREGATE_WRAPPERS_EXTRACTABLE.dependsOn(DIRECT_WRAPPERS_EXTRACTABLE, p -> a -> Collections.singleton(p.apply(a)));
		DIRECT_WRAPPERS_EXTRACTABLE.dependsOn(AGGREGATE_WRAPPERS_EXTRACTABLE, p -> a -> getOnly(p.apply(a)));
		AGGREGATE_WRAPPERS_INSERTABLE.dependsOn(DIRECT_WRAPPERS_INSERTABLE, p -> a -> Collections.singleton(p.apply(a)));
		DIRECT_WRAPPERS_INSERTABLE.dependsOn(AGGREGATE_WRAPPERS_INSERTABLE, p -> a -> getOnly(p.apply(a)));
		AGGREGATE_WRAPPERS_INSERTABLE.dependsOn(AGGREGATE_WRAPPERS, p -> i -> {
			if (i instanceof Participant) {
				return (Collection) p.apply((Participant) i);
			}
			return null;
		});
		AGGREGATE_WRAPPERS_EXTRACTABLE.dependsOn(AGGREGATE_WRAPPERS, p -> i -> {
			if (i instanceof Participant) {
				return (Collection) p.apply((Participant) i);
			}
			return null;
		});
	}

	private final boolean empty, full;

	Participants(boolean empty, boolean full) {
		this.empty = empty;
		this.full = full;
	}

	/**
	 * unwraps a delegate recursively
	 */
	@NotNull
	public static <T> Collection<T> unwrap(Access<AccessFunction<T, Collection<T>>, Collection<T>> access, T instance) {
		Collection<T> collection = unwrapInternal(access.get(), instance);
		if (collection == null) {
			return Collections.singleton(instance);
		}
		return collection;
	}

	/**
	 * @return null if the instance is not a wrapper
	 */
	@Nullable
	public static <T> Collection<T> unwrapInternal(AccessFunction<T, Collection<T>> func, T instance) {
		Collection<T> starting = func.apply(instance);
		if (starting == null) {
			return null;
		} else {
			List<T> valid = new ArrayList<>();
			for (T t : starting) {
				Collection<T> unwrapped = unwrapInternal(func, t);
				if (unwrapped == null) {
					valid.add(t);
				} else {
					valid.addAll(unwrapped);
				}
			}
			return valid;
		}
	}

	private static <T> T getOnly(Iterable<T> val) {
		Iterator<T> iter = val.iterator();
		if(!iter.hasNext()) return null;
		T first = iter.next();
		// if more than one element, then we can't reliably add compat, so ignore it
		if(iter.hasNext()) return null;
		return first;
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
