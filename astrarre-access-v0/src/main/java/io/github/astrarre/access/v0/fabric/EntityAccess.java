package io.github.astrarre.access.v0.fabric;

import java.util.Objects;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.access.v0.fabric.helper.EntityAccessHelper;
import io.github.astrarre.util.v0.api.func.ArrayFunc;
import io.github.astrarre.util.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.fabric.func.EntityFunction;
import io.github.astrarre.access.v0.fabric.provider.EntityProvider;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;

public class EntityAccess<T> extends Access<EntityFunction<T>> {
	public final EntityAccessHelper<EntityFunction<T>> helper;

	public EntityAccess(Id id) {
		this(id, (T) null);
	}

	/**
	 * docs for each of the constructors are the same from FunctionAccess
	 * @see FunctionAccess
	 */
	public EntityAccess(Id id, T defaultValue) {
		this(id, EntityFunction.skipIfNull(defaultValue), (direction, entity) -> null);
	}

	public EntityAccess(Id id, ArrayFunc<EntityFunction<T>> iterFunc) {
		this(id, iterFunc, iterFunc.empty());
	}

	public EntityAccess(Id id, ArrayFunc<EntityFunction<T>> iterFunc, EntityFunction<T> empty) {
		super(id, iterFunc);
		this.helper = new EntityAccessHelper<>(this.funcFilter_(Entity.class, empty));
	}

	public static <T> EntityAccess<T> newInstance(Id id, IterFunc<T> combiner) {
		return new EntityAccess<>(id, functions -> (d, e) -> transform(functions, f -> f.get(d, e), combiner));
	}

	private boolean addedProviderFunction, addedInstanceofFunction;

	/**
	 * The entity advanced filtering helper. It is recommended you use these for performance's sake
	 */
	public EntityAccessHelper<EntityFunction<T>> getEntityHelper() {
		return this.helper;
	}

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
		this.helper.getEntity().forGenericProvider(this);
		return this;
	}

	/**
	 * adds a function for if entity instanceof B, return entity
	 */
	public EntityAccess<T> addInstanceOfFunction(TypeToken<T> type) {
		if(this.addedInstanceofFunction) return this;
		this.addedInstanceofFunction = true;
		this.andThen((direction, entity) -> {
			if (entity != null && type.isSupertypeOf(entity.getClass())) {
				return (T) entity;
			}
			return null;
		});
		return this;
	}

	/**
	 * filters for entities of the given entity type
	 * @see #getEntityHelper()
	 * @see EntityAccessHelper#getEntityType()
	 * @see FunctionAccessHelper#forInstanceWeak(Object, Object)
	 */
	public EntityAccess<T> forType(EntityType<?> type, EntityFunction<T> function) {
		this.getEntityHelper().getEntityType().forInstanceWeak(type, function);
		return this;
	}

	/**
	 * filters for entities that have a specific item in the specified equipment slot
	 * @see #getEntityHelper()
	 */
	public EntityAccess<T> ifHas(EquipmentSlot slot, Item key, EntityFunction<T> function) {
		this.getEntityHelper().getForEquipment(slot).getItem().forInstanceWeak(key, function);
		return this;
	}

}
