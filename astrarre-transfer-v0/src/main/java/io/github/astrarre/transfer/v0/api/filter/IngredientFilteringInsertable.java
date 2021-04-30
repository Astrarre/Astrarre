package io.github.astrarre.transfer.v0.api.filter;

import java.util.Set;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.mixin.IngredientAccess;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class IngredientFilteringInsertable extends FilteringInsertable<ItemKey> implements Provider {
	protected final Ingredient ingredient;
	protected Set<Item> items;
	public IngredientFilteringInsertable(Ingredient ingredient, Insertable<ItemKey> delegate) {
		super((object, quantity) -> ingredient.test(object.createItemStack(quantity)), delegate);
		this.ingredient = ingredient;
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		if(access == FabricParticipants.ITEM_FILTERS) {
			if(this.items == null) {
				((IngredientAccess) (Object) this.ingredient).astrarre_transfer$callCacheMatchingStacks();
				ItemStack[] stacks = ((IngredientAccess) (Object) this.ingredient).astrarre_transfer$getMatchingStacks();
				ObjectOpenHashSet<Item> items = new ObjectOpenHashSet<>(stacks.length);
				for (ItemStack stack : stacks) {
					items.add(stack.getItem());
				}
				items.trim();
				this.items = items;
				return items;
			}
			return this.items;
		}
		return null;
	}
}
