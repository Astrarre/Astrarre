package io.github.astrarre.transfer.v0.lba.item;

import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.transfer.v0.lba.RefAndConsumerParticipant;
import io.github.astrarre.transfer.v0.lba.adapters.LBAItemApiApiContext;
import io.github.astrarre.transfer.v0.lba.fluid.FluidExtractableExtractable;
import io.github.astrarre.transfer.v0.lba.fluid.FluidInsertableInsertable;
import io.github.astrarre.transfer.v0.lba.fluid.ParticipantFluidExtractable;
import io.github.astrarre.transfer.v0.lba.fluid.ParticipantFluidInsertable;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

@ApiStatus.Experimental
public class LBAItemsCompat {
	public static boolean inExtractableItemsCompat, inInsertableItemsCompat;
	public static boolean inExtractableBlocksCompat, inInsertableBlocksCompat;
	static {
		ItemAttributes.EXTRACTABLE.appendItemAdder((Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess,
				ItemAttributeList<ItemExtractable> list) -> {
			boolean inExtractable = inExtractableItemsCompat;
			inExtractableItemsCompat = true;
			RefAndConsumerParticipant participant = new RefAndConsumerParticipant(stack, excess);
			ItemStack current = stack.get();
			list.add(new ParticipantItemExtractable(FabricParticipants.ITEM_ITEM.get().get(null, ItemKey.of(current), current.getCount(), participant)));
			inExtractableItemsCompat = inExtractable;
		});
		ItemAttributes.INSERTABLE.appendItemAdder((stack, excess, to) -> {
			boolean inInsertable = inInsertableItemsCompat;
			inInsertableItemsCompat = true;
			RefAndConsumerParticipant participant = new RefAndConsumerParticipant(stack, excess);
			ItemStack current = stack.get();
			to.add(new ParticipantItemInsertable(FabricParticipants.ITEM_ITEM.get().get(null, ItemKey.of(current), current.getCount(), participant)));
			inInsertableItemsCompat = inInsertable;
		});
		ItemAttributes.EXTRACTABLE.appendBlockAdder((world, pos, state, to) -> {
			boolean inInsertable = inExtractableBlocksCompat;
			inExtractableBlocksCompat = true;
			to.add(new ParticipantItemExtractable(FabricParticipants.ITEM_WORLD.get().get(to.getSearchDirection(), state, world, pos)));
			inExtractableBlocksCompat = inInsertable;
		});
		ItemAttributes.INSERTABLE.appendBlockAdder((world, pos, state, to) -> {
			boolean inInsertable = inInsertableBlocksCompat;
			inInsertableBlocksCompat = true;
			to.add(new ParticipantItemInsertable(FabricParticipants.ITEM_WORLD.get().get(to.getSearchDirection(), state, world, pos)));
			inInsertableBlocksCompat = inInsertable;
		});
		FabricParticipants.ITEM_ITEM.andThen((direction, key, count, container) -> {
			if(inExtractableItemsCompat || inInsertableItemsCompat) {
				return null;
			}

			LBAItemApiApiContext context = new LBAItemApiApiContext(container, key.createItemStack(count));
			ItemExtractable extractable = ItemAttributes.EXTRACTABLE.get(context, context);
			ItemInsertable insertable = ItemAttributes.INSERTABLE.get(context, context);
			ItemExtractableExtractable extract = new ItemExtractableExtractable(extractable);
			ItemInsertableInsertable insert = new ItemInsertableInsertable(insertable);
			return Participant.of(extract, insert);
		});
		FabricParticipants.ITEM_WORLD.andThen((WorldFunction.NoBlock<Participant<ItemKey>>) (direction, world, pos) -> {
			if(inExtractableBlocksCompat || inInsertableBlocksCompat) {
				return null;
			}

			ItemExtractable extractable = ItemAttributes.EXTRACTABLE.get(world, pos, SearchOptions.inDirection(direction));
			ItemInsertable insertable = ItemAttributes.INSERTABLE.get(world, pos, SearchOptions.inDirection(direction));
			ItemExtractableExtractable extract = new ItemExtractableExtractable(extractable);
			ItemInsertableInsertable insert = new ItemInsertableInsertable(insertable);
			return Participant.of(extract, insert);
		});
	}

	public static void init() {}
}
