package io.github.astrarre.testmod;

import io.github.astrarre.v0.block.Block;
import io.github.astrarre.v0.block.Material;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println(net.minecraft.block.Block.class);
		System.exit(0);
	}
}
