package insane_crafting;

import java.awt.Point;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class InsaneCraftingSlot implements Inventory {
	final InsaneInventory insane;
	World world;
	ItemStack previewStack;

	public InsaneCraftingSlot(InsaneInventory inventory) {
		this.insane = inventory;
		inventory.change.andThen(() -> this.previewStack = null);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.getStack(0).isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		this.computePreviewStack();
		return this.previewStack;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack get = this.getStack(0);
		if(amount >= get.getCount()) {
			for(int i = 0; i < this.insane.size(); i++) {
				this.insane.getStack(i).decrement(1);
			}
			return get;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, Integer.MAX_VALUE);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if(stack.isEmpty()) {
			this.removeStack(slot, Integer.MAX_VALUE);
		} else if(!ItemStack.areEqual(this.getStack(0), stack)) {
			System.err.println("Attempted to set stack in crafting slot!");
			new Throwable().printStackTrace();
		}
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public void clear() {
		this.setStack(0, ItemStack.EMPTY);
	}

	public String exportRecipe() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
		for(int i = this.insane.first; i <= this.insane.last; i++) {
			ItemStack stack = this.insane.getStack(i);
			if(!stack.isEmpty()) {
				Point point = InsaneInventory.pointFromIndex(i);
				minX = Math.min(point.x, minX);
				minY = Math.min(point.y, minY);
				maxX = Math.max(point.x, maxX);
				maxY = Math.max(point.y, maxY);
			}
		}

		JsonArray array = new JsonArray();
		for(int y = minY; y <= maxY; y++) {
			JsonArray row = new JsonArray();
			array.add(row);
			for(int x = minX; x <= maxX; x++) {
				JsonObject object = new JsonObject();
				int index = InsaneInventory.indexFromPoint(x, y);
				if(index != -1) {
					ItemStack stack = this.insane.getStack(index);
					if(!stack.isEmpty()) {
						String itemId = Registry.ITEM.getId(stack.getItem()).toString();
						object.addProperty("item", itemId);
					}
				}
				row.add(object);
			}
		}

		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "insane_crafting:craft");
		recipe.add("input", array);
		recipe.addProperty("output", "%placeholder%");
		return recipe.toString();
	}

	void computePreviewStack() {
		if(this.insane.first != Integer.MAX_VALUE) {
			int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
			for(int i = this.insane.first; i < this.insane.last; i++) {
				Point point = InsaneInventory.pointFromIndex(i);
				minX = Math.min(point.x, minX);
				minY = Math.min(point.y, minY);
				maxX = Math.max(point.x, maxX);
				maxY = Math.max(point.y, maxY);
			}

			if(minX == Integer.MAX_VALUE || minY == Integer.MAX_VALUE) {
				this.previewStack = ItemStack.EMPTY;
				return;
			}

			outer:
			for(InsaneCraftingRecipe recipe : InsaneCrafting.RECIPES) {
				for(int y = 0; y < recipe.input.size(); y++) {
					var ingredients = recipe.input.get(y);
					for(int x = 0; x < ingredients.size(); x++) {
						Ingredient ingredient = ingredients.get(x);
						int itemIndex = InsaneInventory.indexFromPoint(minX + x, minY + y);
						if(itemIndex == -1 && !ingredient.isEmpty()) {
							continue outer;
						} else if(itemIndex != -1 && !ingredient.test(this.insane.getStack(itemIndex))) {
							continue outer;
						}
					}
				}

				this.previewStack = recipe.output.copy();
				return;
			}
		}

		this.previewStack = ItemStack.EMPTY;
	}
}
