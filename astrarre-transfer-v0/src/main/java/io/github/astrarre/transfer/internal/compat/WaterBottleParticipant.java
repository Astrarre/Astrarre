package io.github.astrarre.transfer.internal.compat;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

public class WaterBottleParticipant extends BucketItemParticipant {
	private static final ItemKey WATER = ItemKey.of(PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
	public WaterBottleParticipant(ItemKey key, int quantity, ReplacingParticipant<ItemKey> container) {
		super(key, quantity, container);
	}

	@Override
	protected boolean isValid(Fluid fluid) {
		return fluid == Fluids.WATER;
	}

	@Override
	protected ItemKey filledItem(Fluid fluid) {
		return WATER;
	}

	@Override
	protected ItemKey emptyItem() {
		return ItemKey.of(Items.GLASS_BOTTLE);
	}

	@Override
	protected Fluid get(ItemKey item) {
		Item i = item.getItem();
		if(i == Items.GLASS_BOTTLE) {
			return Fluids.EMPTY;
		} else if(WATER.equals(item)) {
			return Fluids.WATER;
		}
		return Fluids.EMPTY;
	}

	@Override
	protected int quantity(ItemKey key) {
		return Droplet.BOTTLE;
	}
}
