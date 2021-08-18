package io.github.astrarre.gui.v1.api.component.icon;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.item.ModelTransformType;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ItemIcon implements Icon {
	final int msPerItem;
	final Iterator<ItemStack> items;
	ItemStack current;
	long lastNext;


	/**
	 * @param msPerItem how many miliseconds to show each item
	 */
	public ItemIcon(int msPerItem, Iterable<ItemStack> items) {
		this.items = Iterators.cycle(items);
		this.msPerItem = msPerItem;
	}

	public ItemIcon(int msPerItem, ItemStack... stacks) {
		this(msPerItem, Arrays.asList(stacks));
	}

	public ItemIcon(Iterable<Item> items, int msPerItem) {
		this(msPerItem, Iterables.transform(items, ItemStack::new));
	}

	public ItemIcon(int msPerItem, Item... items) {
		this(Arrays.asList(items), msPerItem);
	}

	@Override
	public float height() {
		return 16;
	}

	@Override
	public float width() {
		return 16;
	}

	@Override
	public void render(Render3d render) {
		long since = System.currentTimeMillis() - this.lastNext;
		ItemStack stack = this.current;
		if(since > this.msPerItem || stack == null) {
			if(this.items.hasNext()) {
				this.current = stack = this.items.next();
			} else {
				stack = ItemStack.EMPTY;
			}
		}

		render.item().render(ModelTransformType.Standard.GUI, stack);
	}
}
