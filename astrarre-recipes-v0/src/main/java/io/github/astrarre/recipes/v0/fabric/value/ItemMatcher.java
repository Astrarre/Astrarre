package io.github.astrarre.recipes.v0.fabric.value;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.recipes.internal.mixin.SetTagAccess;
import io.github.astrarre.recipes.internal.mixin.TagDelegateAccess;
import io.github.astrarre.recipes.internal.mixin.TagWrapperAccess;
import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class ItemMatcher {
	protected final Either<Item, Tag<Item>> itemMatch;
	protected final NBTagView nbt;

	public ItemMatcher(Either<Item, Tag<Item>> match, @Nullable NBTagView nbt) {
		this.itemMatch = match;
		this.nbt = nbt;
	}

	public boolean matches(ItemKey stack) {
		boolean validItem = false;
		if (this.itemMatch.hasRight() && this.itemMatch.getRight().contains(stack.getItem())) {
			validItem = true;
		} else if(stack.getItem() == this.itemMatch.getLeft()) {
			validItem = true;
		}

		if(validItem && this.nbt != null) {
			return this.nbt.equals(stack.getTag());
		} else {
			return validItem;
		}
	}

	public static final class Parser implements ValueParser<ItemMatcher> {
		@Override
		public Either<ItemMatcher, String> parse(PeekableReader reader) {
			Either<Item, Tag<Item>> itemMatch;
			if(reader.peek() == '#') {
				Either<Tag<Item>, String> either = FabricValueParsers.ITEM_TAG.parse(reader);
				if(either.hasLeft()) {
					itemMatch = Either.ofRight(either.getLeft());
				} else {
					return either.asLeft();
				}
			} else {
				Either<Item, String> either = FabricValueParsers.ITEM.parse(reader);
				if(either.hasLeft()) {
					itemMatch = Either.ofLeft(either.getLeft());
				} else {
					return either.asLeft();
				}
			}

			NBTagView nbt = NBTagView.EMPTY;
			ValueParser.skipWhitespace(reader, 10);
			if(reader.peek() == '{') {
				Either<NBTagView, String> either = ValueParser.NBT.parse(reader);
				if(either.hasLeft()) {
					nbt = either.getLeft();
				} else {
					return either.asLeft();
				}
			}
			return Either.ofLeft(new ItemMatcher(itemMatch, nbt));
		}
	}

	public NBTagView nbt() {
		return this.nbt;
	}

	/**
	 * @return a heuristic for finding items
	 */
	public Set<Item> items() {
		if (this.itemMatch.hasLeft()) {
			return Collections.singleton(this.itemMatch.getLeft());
		}

		Tag<Item> root = findRoot(this.itemMatch.getRight());
		if (root instanceof SetTagAccess) {
			return ((SetTagAccess) root).getValueSet();
		} else {
			return new AbstractSet<Item>() {
				@Override
				public Iterator<Item> iterator() {
					return root.values().iterator();
				}

				@Override
				public int size() {
					return root.values().size();
				}
			};
		}
	}

	public static <T> Tag<T> findRoot(Tag<T> root) {
		while (true) {
			if (root instanceof TagWrapperAccess) {
				root = ((TagWrapperAccess) root).callGet();
			} else if (root instanceof TagDelegateAccess) {
				root = ((TagDelegateAccess) root).callGetTag();
			} else {
				return root;
			}
		}
	}
}
