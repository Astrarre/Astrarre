package io.github.astrarre.transfer.internal;

import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;

import net.minecraft.item.ItemStack;

import net.fabricmc.api.ModInitializer;

public class AstrarreTransferInitInternal implements ModInitializer {
	public static boolean inLbaCompat;
	@Override
	public void onInitialize() {
		ItemAttributes.EXTRACTABLE.appendItemAdder((Reference<ItemStack> reference, LimitedConsumer<ItemStack> consumer,
				ItemAttributeList<ItemExtractable> list) -> {
			inLbaCompat = true;
			// extraction can only be done on Reference
			// insertion can be done on both.. hmm
			//  perhaps the best way to do this is a "smart" system that tries it's best to bridge the gap
			inLbaCompat = false;
		});
	}
}
