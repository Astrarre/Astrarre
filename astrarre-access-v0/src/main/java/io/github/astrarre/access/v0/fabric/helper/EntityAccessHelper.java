package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
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

	public EntityAccessHelper(AccessHelpers.Context<Entity, F> copyFrom) {
		super(copyFrom);
		this.entity = new FunctionAccessHelper<>(copyFrom);
		this.entityType = new FunctionAccessHelper<>(copyFrom.map(Entity::getType));
		this.entityTag = new TaggedAccessHelper<>(copyFrom.map(Entity::getType));
		this.entityTypeRegistry = new RegistryAccessHelper<>(Registry.ENTITY_TYPE, copyFrom.map(Entity::getType));
		for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
			this.equipment[slot.ordinal()] = new ItemAccessHelper<>(copyFrom.map(e -> {
				if (e instanceof LivingEntity l) {
					return l.getEquippedStack(slot).getItem();
				} else {
					return Items.AIR;
				}
			}));
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
