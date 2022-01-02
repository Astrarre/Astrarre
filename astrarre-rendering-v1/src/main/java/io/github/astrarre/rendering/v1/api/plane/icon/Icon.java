package io.github.astrarre.rendering.v1.api.plane.icon;

import java.util.Arrays;
import java.util.function.UnaryOperator;

import com.google.common.collect.Iterables;
import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.plane.icon.backgrounds.SlotBackgroundIcon;
import io.github.astrarre.rendering.v1.api.plane.icon.wrapper.OffsetIcon;
import io.github.astrarre.rendering.v1.api.plane.icon.wrapper.RealignedIcon;
import io.github.astrarre.rendering.v1.api.plane.icon.wrapper.ScaledIcon;
import io.github.astrarre.rendering.v1.api.plane.icon.wrapper.TransformedIcon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.util.v0.api.Edge;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

/**
 * @see Icons
 */
public interface Icon {
	/**
	 * an icon of a slot background
	 */
	static Icon slot(float width, float height) {
		return new SlotBackgroundIcon(width, height);
	}

	static Icon slot(float width, float height, SlotBackgroundIcon.State state) {
		return new SlotBackgroundIcon(width, height, state);
	}

	static Icon color(int color, float width, float height) {
		return new FlatColorIcon(color, 0, 0, width, height);
	}

	static Icon repeat(TextureIcon icon, float repeatX, float repeatY) {
		return new TextureIcon.Repeating(icon, repeatX, repeatY);
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
	static TextureIcon tex(Texture texture, float width, float height) {
		return new TextureIcon(texture, width, height);
	}

	static TextureIcon tex(Texture texture, int color, float width, float height) {
		return new TextureIcon(texture, color, 0, 0, width, height);
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
	static ScrollingLabelIcon scrollingText(Text text, int color, boolean shadow, float width) {
		return new ScrollingLabelIcon(text, color, shadow, width, () -> (int) ((System.currentTimeMillis() % 0xffffff) / 100));
	}

	@Edge
	static ScrollingLabelIcon scrollingText(Text text, float width) {
		return scrollingText(text, 0xffffffff, true, width);
	}

	@Edge
	static ScrollingLabelIcon scrollingText(String text, float width) {
		return scrollingText(new LiteralText(text), 0xffffffff, true, width);
	}

	static Group group(Icon normal, Icon hover, Icon pressed, Icon disabled) {
		return new Group(normal, hover, pressed, disabled);
	}

	static Group group(Icon normal, Icon pressed) {
		return new Group(normal, normal.highlighted(), pressed, normal.darkened());
	}

	/**
	 * @return the width of the icon
	 */
	float width();

	/**
	 * @return the height of the icon
	 */
	float height();

	void render(Render3d render);

	/**
	 * rotate the icon about it's middle, and automatically aligns the 0, 0 of the transformation with the 0, 0 of the new icon
	 */
	default Icon rotateAboutMiddle(AngleFormat format, float theta) {
		return this.transform(Transform2d.rotate(this.width() / 2f, this.height() / 2f, format, theta));
	}

	default Icon rotateAboutMiddleUnaligned(AngleFormat format, float theta) {
		return this.transformUnaligned(Transform2d.rotate(this.width() / 2f, this.height() / 2f, format, theta));
	}

	/**
	 * Transforms the icon, and then moves it's origin to the new icon's origin.
	 */
	default Icon transform(Transform2d transform) {
		return new RealignedIcon(this, transform);
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

	default Icon overlayCentered(Icon icon) {
		return this.andThen(icon.offset((this.width() - icon.width()) / 2, (this.height() - icon.height()) / 2));
	}

	default Icon transformUnaligned(Transform2d transform) {
		return new TransformedIcon(this, transform);
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
		return new OffsetIcon(this, x, y);
	}

	default Icon highlighted() {
		return this.colored(0xaaddddff);
	}

	default Icon darkened() {
		return this.colored(0x77000000);
	}

	/**
	 * creates a new icon with a colored overlay over this one
	 */
	default Icon colored(int argb) {
		return this.andThen(color(argb, this.width(), this.height()));
	}

	/**
	 * @param tileX how many times to tile this icon on the x axis
	 * @return a new icon that is a tiled version of this one
	 */
	default Icon tiled(int tileX, int tileY) {
		return new TiledIcon(this, tileX, tileY);
	}

	record Group(Icon normal, Icon hover, Icon pressed, Icon disabled) {
		public void requireUniformSize() {
			Validate.equals((e, v) -> "Icons must be same dims " + e + " vs " + v, this.normal.height(), this.hover.height(), this.pressed.height(), this.disabled.height());
			Validate.equals((e, v) -> "Icons must be same dims " + e + " vs " + v, this.normal.width(), this.hover.width(), this.pressed.width(), this.disabled.width());
		}

		public Group withOverlay(Icon n) {
			return this.transform(icon -> icon.andThen(n));
		}

		public Group withCenteredOverlay(Icon n) {
			return this.transform(icon -> icon.overlayCentered(n));
		}

		public Group transform(UnaryOperator<Icon> icon) {
			return new Group(icon.apply(this.normal), icon.apply(this.hover), icon.apply(this.pressed), icon.apply(this.disabled));
		}

		public Group withNormal(Icon normal) {
			return new Group(normal, this.hover, this.pressed, this.disabled);
		}

		public Group withHover(Icon hover) {
			return new Group(this.normal, hover, this.pressed, this.disabled);
		}

		public Group withPressed(Icon pressed) {
			return new Group(this.normal, this.hover, pressed, this.disabled);
		}

		public Group withDisabled(Icon disabled) {
			return new Group(this.normal, this.hover, this.pressed, disabled);
		}
	}
}
