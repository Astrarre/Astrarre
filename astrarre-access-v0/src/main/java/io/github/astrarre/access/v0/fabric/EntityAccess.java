package io.github.astrarre.access.v0.fabric;

import io.github.astrarre.access.internal.MapFilter;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.util.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.fabric.func.EntityFunction;
import io.github.astrarre.access.v0.fabric.provider.EntityProvider;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

import net.minecraft.util.Pair;

public class EntityAccess<T> extends Access<EntityFunction<T>> {
	private final MapFilter<EntityType<?>, EntityFunction<T>> entityTypes;
	private final MapFilter<Pair<EquipmentSlot, Item>, EntityFunction<T>> equipmentFilters;

	public EntityAccess(Id id) {
		this(id, (T) null);
	}

	/**
	 * docs for each of the constructors are the same from FunctionAccess
	 * @see FunctionAccess
	 */
	public EntityAccess(Id id, T defaultValue) {
		this(id, (functions) -> (d, e) -> {
			for (EntityFunction<T> function : functions) {
				T val = function.get(d, e);
				if (val != null) {
					return val;
				}
			}
			return defaultValue;
		});
	}

	public EntityAccess(Id id, IterFunc<EntityFunction<T>> iterFunc) {
		super(id, iterFunc);
		this.entityTypes = new MapFilter<>(iterFunc);
		this.equipmentFilters = new MapFilter<>(iterFunc);
	}

	/**
	 * @param combiner combines the return value of the function
	 */
	public static <T> EntityAccess<T> newInstance(Id id, IterFunc<T> combiner) {
		return new EntityAccess<>(id, (functions) -> (d, e) -> {
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
