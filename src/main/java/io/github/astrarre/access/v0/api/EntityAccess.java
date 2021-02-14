package io.github.astrarre.access.v0.api;

import java.util.function.BinaryOperator;

import io.github.astrarre.access.internal.util.MapFilter;
import io.github.astrarre.access.v0.api.func.EntityFunction;
import io.github.astrarre.access.v0.api.provider.EntityProvider;
import io.github.astrarre.v0.entity.EntityType;
import io.github.astrarre.v0.entity.EquipmentSlot;
import io.github.astrarre.v0.entity.LivingEntity;
import io.github.astrarre.v0.item.Item;

import net.minecraft.util.Pair;

public class EntityAccess<T> extends Access<EntityFunction<T>, T> {
	private final MapFilter<EntityType<?>, EntityFunction<T>, T> entityTypes;
	private final MapFilter<Pair<EquipmentSlot, Item>, EntityFunction<T>, T> equipmentFilters;

	/**
	 * docs for each of the constructors are the same from FunctionAccess
	 * @see FunctionAccess
	 */
	public EntityAccess() {
		this((EntityFunction<T>) (d, e) -> null);
	}

	public EntityAccess(EntityFunction<T> defaultAccess) {
		this((function, function2) -> (d, e) -> {
			T val = function.get(d, e);
			if (val != null) {
				return val;
			}
			return function2.get(d, e);
		}, defaultAccess);
	}

	public EntityAccess(BinaryOperator<EntityFunction<T>> andThen, EntityFunction<T> defaultAccess) {
		super(andThen, defaultAccess);
		this.entityTypes = new MapFilter<>(andThen, EntityFunction.empty());
		this.equipmentFilters = new MapFilter<>(andThen, EntityFunction.empty());
	}

	public EntityAccess(T defaultValue) {
		this((EntityFunction<T>) (d, e) -> defaultValue);
	}

	public EntityAccess(BinaryOperator<EntityFunction<T>> andThen) {
		this(andThen, (d, e) -> null);
	}

	public EntityAccess(BinaryOperator<EntityFunction<T>> andThen, T defaultValue) {
		this(andThen, (d, e) -> defaultValue);
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <T> EntityAccess<T> newInstance(BinaryOperator<T> combiner) {
		return new EntityAccess<>((BinaryOperator<EntityFunction<T>>) (function, function2) -> (d, e) -> combiner.apply(function.get(d, e), function2.get(d, e)));
	}

	public static <T> EntityAccess<T> newInstance(BinaryOperator<T> combiner, T defaultValue) {
		return new EntityAccess<>((function, function2) -> (d, e) -> combiner.apply(function.get(d, e), function2.get(d, e)), defaultValue);
	}

	/**
	 * adds an entity function for {@link EntityProvider}
	 * @see EntityProvider
	 */
	public EntityAccess<T> addEntityProviderFunction() {
		this.andThen((direction, entity) -> {
			if (entity instanceof EntityProvider) {
				return ((EntityProvider) entity).get(this, direction);
			}
			return null;
		});
		return this;
	}

	/**
	 * filters for entities of the given entity type
	 */
	public EntityAccess<T> forType(EntityType<?> type, EntityFunction<T> function) {
		if(this.entityTypes.add(type, function)) {
			this.andThen((d, e) -> this.entityTypes.get(e.getType()).get(d, e));
		}
		return this;
	}

	/**
	 * filters for entities that have a specific item in the specified equipment slot
	 */
	public EntityAccess<T> ifHas(EquipmentSlot slot, Item key, EntityFunction<T> function) {
		if (this.equipmentFilters.add(new Pair<>(slot, key), function)) {
			for (EquipmentSlot value : EquipmentSlot.values()) {
				this.andThen((d, e) -> {
					if(e instanceof LivingEntity) {
						return this.equipmentFilters.get(new Pair<>(value, ((LivingEntity)e).getEquippedStack(slot).getItem())).get(d, e);
					}
					return null;
				});
			}
		}
		return this;
	}
}
