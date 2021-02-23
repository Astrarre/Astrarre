package io.github.astrarre.access.v0.api;

import io.github.astrarre.access.internal.MapFilter;
import io.github.astrarre.access.v0.api.func.EntityFunction;
import io.github.astrarre.access.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.api.provider.EntityProvider;
import io.github.astrarre.v0.entity.EntityType;
import io.github.astrarre.v0.entity.EquipmentSlot;
import io.github.astrarre.v0.entity.LivingEntity;
import io.github.astrarre.v0.item.Item;

import net.minecraft.util.Pair;

public class EntityAccess<T> extends Access<EntityFunction<T>> {
	private final MapFilter<EntityType<?>, EntityFunction<T>, T> entityTypes;
	private final MapFilter<Pair<EquipmentSlot, Item>, EntityFunction<T>, T> equipmentFilters;


	/**
	 * docs for each of the constructors are the same from FunctionAccess
	 * @see FunctionAccess
	 */
	public EntityAccess() {
		this((functions) -> (d, e) -> {
			for (EntityFunction<T> function : functions) {
				T val = function.get(d, e);
				if (val != null) {
					return val;
				}
			}
			return null;
		});
	}

	public EntityAccess(IterFunc<EntityFunction<T>> iterFunc) {
		super(iterFunc);
		this.entityTypes = new MapFilter<>(iterFunc);
		this.equipmentFilters = new MapFilter<>(iterFunc);
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <T> EntityAccess<T> newInstance(IterFunc<T> combiner) {
		return new EntityAccess<>((functions) -> (d, e) -> {
			for (EntityFunction<T> function : functions) {
				T val = function.get(d, e);
				if (val != null) {
					return val;
				}
			}
			return null;
		});
	}

	private boolean addedProviderFunction;

	/**
	 * adds an entity function for {@link EntityProvider}
	 * (calling this multiple times will only register it once)
	 * @see EntityProvider
	 */
	public EntityAccess<T> addEntityProviderFunction() {
		if(this.addedProviderFunction) return this;
		this.addedProviderFunction = true;
		this.andThen((direction, entity) -> {
			if (entity instanceof EntityProvider) {
				return (T) ((EntityProvider) entity).get(this, direction);
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
