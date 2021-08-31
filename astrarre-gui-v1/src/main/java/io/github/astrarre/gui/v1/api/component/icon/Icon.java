package io.github.astrarre.gui.v1.api.component.icon;

import java.util.Arrays;

import com.google.common.collect.Iterables;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.util.GuiRenderable;
import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.Edge;
import io.github.astrarre.util.v0.api.SafeCloseable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

/**
 * @see Icons
 */
public interface Icon extends GuiRenderable {
	static Icon color(int color, float width, float height) {
		return new FlatColorIcon(color, 0, 0, width, height);
	}

	/**
	 * @return an icon that cycles between displaying each of the items
	 */
	@Edge
	static Icon item(Item... items) {
		return item(Math.max(5000 / items.length, 500), items);
	}

	/**
	 * @see #item(Item...)
	 */
	@Edge
	static Icon item(ItemStack... stacks) {
		return item(Math.max(5000 / stacks.length, 500), stacks);
	}

	/**
	 * @param msPerItem the number of milliseconds to show each item
	 * @see #item(Item...)
	 */
	@Edge
	static Icon item(int msPerItem, Item... items) {
		return items(msPerItem, Arrays.asList(items));
	}

	/**
	 * @param msPerItem the number of milliseconds to show each item
	 * @see #item(Item...)
	 */
	@Edge
	static Icon item(int msPerItem, ItemStack... stacks) {
		return stacks(msPerItem, Arrays.asList(stacks));
	}

	/**
	 * @param msPerItem the number of milliseconds to show each item
	 * @see #item(Item...)
	 */
	@Edge
	static Icon stacks(int msPerItem, Iterable<ItemStack> stacks) {
		return new ItemIcon(msPerItem, stacks);
	}

	/**
	 * @param msPerItem the number of milliseconds to show each item
	 * @see #item(Item...)
	 */
	@Edge
	static Icon items(int msPerItem, Iterable<Item> items) {
		return new ItemIcon(msPerItem, Iterables.transform(items, ItemStack::new));
	}

	/**
	 * @param texture the texture to display
	 * @param width how large to show the item
	 */
	static Icon tex(Texture texture, float width, float height) {
		return new TextureIcon(texture, width, height);
	}

	@Edge
	static Icon text(OrderedText text, int color, boolean shadow) {
		return new LabelIcon(text, color, shadow);
	}

	@Edge
	static Icon text(OrderedText text) {
		return new LabelIcon(text, 0xffffffff, true);
	}

	@Edge
	static ScrollingLabelIcon scrollingText(Text text, int color, boolean shadow, int width) {
		return new ScrollingLabelIcon(text, color, shadow, width, () -> (int) ((System.currentTimeMillis() % 0xffffff) / 100));
	}

	@Edge
	static ScrollingLabelIcon scrollingText(Text text, int width) {
		return scrollingText(text, 0xffffffff, true, width);
	}

	static Group group(Icon normal, Icon hover, Icon pressed, Icon disabled) {
		return new Group(normal, hover, pressed, disabled);
	}

	/**
	 * @return the height of the icon
	 */
	float height();

	/**
	 * @return the width of the icon
	 */
	float width();

	void render(Render3d render);

	@Override
	default void render(Cursor cursor, Render3d render) {
		this.render(render);
	}

	default Icon asSize(float width, float height) {
		return this.scale(width / this.height(), height / this.width());
	}

	default Icon asSize(float dimensions) {
		return this.asSize(dimensions, dimensions);
	}

	default Icon scale(float scaleX, float scaleY) {
		return new ScaledIcon(this, scaleX, scaleY);
	}

	default Icon scale(float scale) {
		return this.scale(scale, scale);
	}

	/**
	 * overlays the given icon ontop of the current one
	 */
	default Icon andThen(Icon icon) {
		return new Icon() {
			@Override
			public float height() {
				return Math.max(Icon.this.height(), icon.height());
			}

			@Override
			public float width() {
				return Math.max(Icon.this.width(), icon.width());
			}

			@Override
			public void render(Render3d render) {
				Icon.this.render(render);
				icon.render(render);
			}
		};
	}

	default Icon offset(float x, float y) {
		return new Icon() {
			@Override
			public float height() {
				return Icon.this.height() + y;
			}

			@Override
			public float width() {
				return Icon.this.width() + x;
			}

			@Override
			public void render(Render3d render) {
				try(SafeCloseable ignore = render.translate(x, y)) {
					Icon.this.render(render);
				}
			}
		};
	}

	default Icon highlighted() {
		return this.andThen(color(0xaaddddff, this.width(), this.height()));
	}

	// todo this is deactivated, add that to ogroup
	default Icon darkened() {
		return this.andThen(color(0x77000000, this.width(), this.height()));
	}

	record Group(Icon normal, Icon hover, Icon pressed, Icon disabled) {}

}
