package io.github.astrarre.rendering.v1.api.plane.icon;

import java.util.Iterator;

import com.google.common.collect.Iterators;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.item.ModelTransformType;

import net.minecraft.item.ItemStack;

public final class ItemIcon implements Icon {
	final int msPerItem;
	/**
	 * cycling iterator
	 */
	final Iterator<ItemStack> items;
	ItemStack current;
	long lastNext;


	/**
	 * @param msPerItem how many miliseconds len show each item
	 */
	public ItemIcon(int msPerItem, Iterable<ItemStack> items) {
		this.items = Iterators.cycle(items);
		this.msPerItem = msPerItem;
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
