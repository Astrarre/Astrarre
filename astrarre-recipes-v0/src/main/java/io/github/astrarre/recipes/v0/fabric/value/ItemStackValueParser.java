package io.github.astrarre.recipes.v0.fabric.value;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class ItemStackValueParser implements ValueParser<ItemStack> {
	private static final Id AIR = Id.create("minecraft", "air");

	@Override
	public Either<ItemStack, String> parse(PeekableReader reader) {
		Either<Id, String> either = ValueParser.ID.parse(reader);
		if(either.hasLeft()) {
			Id id = either.getLeft();
			Item item = Registry.ITEM.get(id.to());
			if(item == Items.AIR && !id.equals(AIR)) {
				return Either.ofRight("item not found " + id);
			}

			ValueParser.skipWhitespace(reader, 10);
			ItemStack stack = new ItemStack(item);
			Either<NBTagView, String> tag = ValueParser.NBT.parse(reader);
			if(tag.hasLeft()) {
				NBTagView nbt = tag.getLeft();
				stack.setTag(nbt.copyTag());
			}
			ValueParser.skipWhitespace(reader, 10);
			if(reader.peek() == 'x') {
				reader.read(); // skip x
				Either<Integer, String> val = ValueParser.INTEGER.parse(reader);
				if(val.hasLeft()) {
					stack.setCount(val.getLeft());
				} else {
					return Either.ofRight("invalid stack amount");
				}
			}
			return Either.ofLeft(stack);
		}
		return Either.ofRight("no item id");
	}
}
