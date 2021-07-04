package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class EntityAccessHelper<F> extends AbstractAccessHelper<Entity, F> {
	private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();
	protected final FunctionAccessHelper<Entity, F> entity;
	protected final FunctionAccessHelper<EntityType<?>, F> entityType;
	protected final TaggedAccessHelper<EntityType<?>, F> entityTag;
	protected final RegistryAccessHelper<EntityType<?>, F> entityTypeRegistry;
	protected final ItemAccessHelper<F>[] equipment = new ItemAccessHelper[EQUIPMENT_SLOTS.length];

	public EntityAccessHelper(AbstractAccessHelper<Entity, F> copyFrom) {
		this(copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

	public EntityAccessHelper(Access<F> func, Function<Function<Entity, F>, F> adder, F empty) {
		this(func.combiner, f -> func.andThen(adder.apply(f)), empty);
	}

	public EntityAccessHelper(Access<F> func, Function<Function<Entity, F>, F> adder) {
		this(func, adder, null);
	}

	public EntityAccessHelper(IterFunc<F> func, Consumer<Function<Entity, F>> adder) {
		this(func, adder, null);
	}

	public EntityAccessHelper(IterFunc<F> func, Consumer<Function<Entity, F>> adder, F empty) {
		super(func, adder, empty);
		this.entity = new FunctionAccessHelper<>(this);
		this.entityType = FunctionAccessHelper.create(this, Entity::getType);
		this.entityTag = TaggedAccessHelper.create(this, Entity::getType);
		this.entityTypeRegistry = RegistryAccessHelper.create(Registry.ENTITY_TYPE, this, Entity::getType);

		for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
			this.equipment[slot.ordinal()] = ItemAccessHelper.create(this, e -> {
				if (e instanceof LivingEntity l) {
					return l.getEquippedStack(slot).getItem();
				} else {
					return Items.AIR;
				}
			});
		}
	}

	public ItemAccessHelper<F> getForEquipment(EquipmentSlot slot) {
		return this.equipment[slot.ordinal()];
	}

	public FunctionAccessHelper<Entity, F> getEntity() {
		return this.entity;
	}

	public FunctionAccessHelper<EntityType<?>, F> getEntityType() {
		return this.entityType;
	}

	public TaggedAccessHelper<EntityType<?>, F> getEntityTag() {
		return this.entityTag;
	}
}
