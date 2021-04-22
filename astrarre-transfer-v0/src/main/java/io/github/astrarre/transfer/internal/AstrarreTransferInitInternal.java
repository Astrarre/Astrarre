package io.github.astrarre.transfer.internal;

import io.github.astrarre.transfer.v0.lba.item.LBAItemsCompat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class AstrarreTransferInitInternal implements ModInitializer {

	@Override
	public void onInitialize() {
		// todo optimize specific ItemFilters
		// todo improve ItemExtractable/Insertable compat for FixedInv and Friends
		if(FabricLoader.getInstance().isModLoaded("libblockattributes-items")) {
			// todo enable once done LBAItemsCompat.init();
		}
	}
}
