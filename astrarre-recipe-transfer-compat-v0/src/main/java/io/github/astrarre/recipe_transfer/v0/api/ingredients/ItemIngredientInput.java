package io.github.astrarre.recipe_transfer.v0.api.ingredients;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.util.ItemIngredient;
import io.github.astrarre.recipes.v0.fabric.value.FabricValueParsers;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.Nullable;

public class ItemIngredientInput implements RecipePart<ItemIngredient, Extractable<ItemKey>> {
	@Override
	public ValueParser<ItemIngredient> parser() {
		return FabricValueParsers.ITEM_INGREDIENT;
	}

	@Override
	public boolean test(Extractable<ItemKey> inp, ItemIngredient val) {
		try(Transaction transaction = Transaction.create(false)) {
			ItemIngredientInsertable insertable = new ItemIngredientInsertable(val);
			inp.extract(transaction, insertable);
			return insertable.count == val.amount;
		}
	}

	@Override
	public void apply(Extractable<ItemKey> inp, ItemIngredient val) {
		try(Transaction transaction = Transaction.create(true)) {
			ItemIngredientInsertable insertable = new ItemIngredientInsertable(val);
			inp.extract(transaction, insertable);
		}
	}

	public static final class ItemIngredientInsertable implements Insertable<ItemKey>, Provider {
		public final ItemIngredient ingredient;
		public int count;

		public ItemIngredientInsertable(ItemIngredient ingredient) {
			this.ingredient = ingredient;
		}

		@Override
		public int insert(@Nullable Transaction transaction, ItemKey type, int quantity) {
			if(this.ingredient.matcher.matches(type)) {
				int toInsert = Math.min(this.ingredient.amount - this.count, quantity);
				this.count += toInsert;
				return toInsert;
			}
			return 0;
		}

		@Override
		public boolean isFull(@Nullable Transaction transaction) {
			return this.count == this.ingredient.amount;
		}

		@Override
		public long getVersion() {
			return this.count;
		}

		@Override
		public @Nullable Object get(Access<?> access) {
			return access == FabricParticipants.FILTERS ? this.ingredient.matcher.items() : null;
		}
	}
}
