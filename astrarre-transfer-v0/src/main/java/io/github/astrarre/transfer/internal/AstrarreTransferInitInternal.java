package io.github.astrarre.transfer.internal;

import io.github.astrarre.transfer.v0.lba.fluid.LBAFluidsCompat;
import io.github.astrarre.transfer.v0.lba.item.LBAItemsCompat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class AstrarreTransferInitInternal implements ModInitializer {

	@Override
	public void onInitialize() {
		// todo improve ItemExtractable/Insertable compat for FixedInv and Friends
		if(FabricLoader.getInstance().isModLoaded("libblockattributes-items")) {
			LBAItemsCompat.init();
		}
		if(FabricLoader.getInstance().isModLoaded("libblockattributes-fluids")) {
			LBAFluidsCompat.init();
		}
	}
}
