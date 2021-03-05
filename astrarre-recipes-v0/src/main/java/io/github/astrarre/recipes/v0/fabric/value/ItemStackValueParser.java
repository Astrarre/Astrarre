package io.github.astrarre.recipes.v0.fabric.value;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.recipes.v0.api.util.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class ItemStackValueParser implements ValueParser<ItemStack> {
	private static final Id AIR = Id.newInstance("minecraft", "air");

	@Override
	public Either<ItemStack, String> parse(PeekableReader reader) {
		PeekableReader sub = reader.createSubReader();
		Either<Id, String> either = ValueParser.ID.parse(sub);
		if(either.hasLeft()) {
			Id id = either.getLeft();
			Item item = Registry.ITEM.get(id.to());
			if(item == Items.AIR && !id.equals(AIR)) {
				reader.abort(sub);
				return Either.ofRight("item not found " + id);
			}
			reader.commit(sub);
			ValueParser.skipWhitespace(reader, 10);
			ItemStack stack = new ItemStack(item);
			Either<NBTagView, String> tag = ValueParser.NBT.parse(reader);
			if(tag.hasLeft()) {
				NBTagView nbt = tag.getLeft();
				stack.setTag(nbt.copyTag());
			}
			ValueParser.skipWhitespace(reader, 10);
			if(reader.peek() == 'x') {
				PeekableReader sub2 = reader.createSubReader();
				sub2.read(); // skip x

				Either<Integer, String> val = ValueParser.INTEGER.parse(sub2);
				if(val.hasLeft()) {
					stack.setCount(val.getLeft());
					reader.commit(sub2);
				} else {
					reader.abort(sub2);
					return Either.ofRight("invalid stack amount");
				}
			}

			return Either.ofLeft(stack);
		}
		reader.abort(sub);
		return Either.ofRight("no item id");
	}
}
