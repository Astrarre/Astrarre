package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;

public class EntityAccessHelper<F> {
	private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();
	protected final FunctionAccessHelper<Entity, F> entity;
	protected final FunctionAccessHelper<EntityType<?>, F> entityType;
	protected final TaggedAccessHelper<EntityType<?>, F> entityTag;
	protected final ItemAccessHelper<F>[] equipment = new ItemAccessHelper[EQUIPMENT_SLOTS.length];

	public EntityAccessHelper(IterFunc<F> func, Consumer<Function<Entity, F>> adder) {
		this(func, adder, null);
	}

	public EntityAccessHelper(IterFunc<F> func, Consumer<Function<Entity, F>> adder, F empty) {
		this.entity = new FunctionAccessHelper<>(func, adder, empty);
		this.entityType = FunctionAccessHelper.create(func, adder, Entity::getType, empty);
		this.entityTag = TaggedAccessHelper.create(func, adder, Entity::getType, empty);
		Function<EquipmentSlot, Function<Entity, Item>> slotFunc = s -> e -> {
			if (e instanceof LivingEntity) {
				return ((LivingEntity) e).getEquippedStack(s).getItem();
			} else {
				return Items.AIR;
			}
		};

		for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
			this.equipment[slot.ordinal()] = ItemAccessHelper.create(func, adder, slotFunc.apply(slot), empty);
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
