package io.github.astrarre.transfer.v0.lba.item;

import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.transfer.v0.lba.RefAndConsumerParticipant;
import io.github.astrarre.transfer.v0.lba.adapters.LBAItemApiApiContext;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemStack;

@ApiStatus.Experimental
public class LBAItemsCompat {
	public static boolean inExtractableCompat, inInsertableCompat;
	static {
		ItemAttributes.EXTRACTABLE.appendItemAdder((Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess,
				ItemAttributeList<ItemExtractable> list) -> {
			boolean inExtractable = inExtractableCompat;
			inExtractableCompat = true;
			RefAndConsumerParticipant participant = new RefAndConsumerParticipant(stack, excess);
			ItemStack current = stack.get();
			list.add(new ParticipantItemExtractable(FabricParticipants.ITEM_ITEM.get().get(null, ItemKey.of(current), current.getCount(), participant)));
			inExtractableCompat = inExtractable;
		});
		ItemAttributes.INSERTABLE.appendItemAdder((stack, excess, to) -> {
			boolean inInsertable = inInsertableCompat;
			inInsertableCompat = true;
			RefAndConsumerParticipant participant = new RefAndConsumerParticipant(stack, excess);
			ItemStack current = stack.get();
			to.add(new ParticipantItemInsertable(FabricParticipants.ITEM_ITEM.get().get(null, ItemKey.of(current), current.getCount(), participant)));
			inInsertableCompat = inInsertable;
		});

		/*FabricParticipants.ITEM_ITEM.andThen((direction, key, count, container) -> {
			if(inExtractableCompat || inInsertableCompat) {
				return null;
			}

			LBAItemApiApiContext context = new LBAItemApiApiContext(container, key.createItemStack(count));
			ItemExtractable extractable = ItemAttributes.EXTRACTABLE.get(context, context);
			ItemInsertable insertable = ItemAttributes.INSERTABLE.get(context, context);

		});*/
	}

	public static void init() {}
}
