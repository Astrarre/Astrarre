package io.github.astrarre.transfer.v0.lba.fluid;

import java.math.RoundingMode;

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.util.Quantity;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.transfer.v0.lba.RefAndConsumerParticipant;
import io.github.astrarre.transfer.v0.lba.adapters.LBAItemApiApiContext;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

public class LBAFluidsCompat {
	public static boolean inExtractableItemsCompat, inInsertableItemsCompat;
	public static boolean inExtractableBlocksCompat, inInsertableBlocksCompat;
	static {
		FluidAttributes.EXTRACTABLE.appendItemAdder((stack, excess, list) -> {
			boolean inExtractable = inExtractableItemsCompat;
			inExtractableItemsCompat = true;
			RefAndConsumerParticipant participant = new RefAndConsumerParticipant(stack, excess);
			ItemStack current = stack.get();
			list.add(new ParticipantFluidExtractable(FabricParticipants.FLUID_ITEM.get().get(null, ItemKey.of(current), current.getCount(), participant)));
			inExtractableItemsCompat = inExtractable;
		});
		FluidAttributes.INSERTABLE.appendItemAdder((stack, excess, to) -> {
			boolean inInsertable = inInsertableItemsCompat;
			inInsertableItemsCompat = true;
			RefAndConsumerParticipant participant = new RefAndConsumerParticipant(stack, excess);
			ItemStack current = stack.get();
			to.add(new ParticipantFluidInsertable(FabricParticipants.FLUID_ITEM.get().get(null, ItemKey.of(current), current.getCount(), participant)));
			inInsertableItemsCompat = inInsertable;
		});
		FluidAttributes.EXTRACTABLE.appendBlockAdder((world, pos, state, to) -> {
			boolean inInsertable = inExtractableBlocksCompat;
			inExtractableBlocksCompat = true;
			to.add(new ParticipantFluidExtractable(FabricParticipants.FLUID_WORLD.get().get(to.getSearchDirection(), state, world, pos)));
			inExtractableBlocksCompat = inInsertable;
		});
		FluidAttributes.INSERTABLE.appendBlockAdder((world, pos, state, to) -> {
			boolean inInsertable = inInsertableBlocksCompat;
			inInsertableBlocksCompat = true;
			to.add(new ParticipantFluidInsertable(FabricParticipants.FLUID_WORLD.get().get(to.getSearchDirection(), state, world, pos)));
			inInsertableBlocksCompat = inInsertable;
		});
		FabricParticipants.FLUID_ITEM.andThen((direction, key, count, container) -> {
			if(inExtractableItemsCompat || inInsertableItemsCompat) {
				return null;
			}

			LBAItemApiApiContext context = new LBAItemApiApiContext(container, key.createItemStack(count));
			FluidExtractable extractable = FluidAttributes.EXTRACTABLE.get(context, context);
			FluidInsertable insertable = FluidAttributes.INSERTABLE.get(context, context);
			// todo reserve stacks?? idfk this is p a i n
			FluidExtractableExtractable extract = new FluidExtractableExtractable(extractable, true);
			FluidInsertableInsertable insert = new FluidInsertableInsertable(insertable, true);
			return Participant.of(extract, insert);
		});
		FabricParticipants.FLUID_WORLD.andThen((WorldFunction.NoBlock<Participant<Fluid>>) (direction, world, pos) -> {
			if(inExtractableBlocksCompat || inInsertableBlocksCompat) {
				return null;
			}

			FluidExtractable extractable = FluidAttributes.EXTRACTABLE.get(world, pos, SearchOptions.inDirection(direction));
			FluidInsertable insertable = FluidAttributes.INSERTABLE.get(world, pos, SearchOptions.inDirection(direction));
			FluidExtractableExtractable extract = new FluidExtractableExtractable(extractable, true);
			FluidInsertableInsertable insert = new FluidInsertableInsertable(insertable, true);
			return Participant.of(extract, insert);
		});
	}

	public static void init() {}

	public static FluidVolume of(Quantity<Fluid> quantity) {
		return FluidKeys.get(quantity.type).withAmount(FluidAmount.of(quantity.amount, Droplet.BUCKET));
	}

	public static Quantity<Fluid> of(FluidVolume volume) {
		return Quantity.of(volume.getRawFluid(), volume.getAmount_F().asInt(Droplet.BUCKET, RoundingMode.FLOOR));
	}
}
