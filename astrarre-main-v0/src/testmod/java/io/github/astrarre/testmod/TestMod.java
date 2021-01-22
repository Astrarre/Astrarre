package io.github.astrarre.testmod;

import io.github.astrarre.v0.block.BaseBlock;
import io.github.astrarre.v0.block.Block;
import io.github.astrarre.v0.block.Material;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println(net.minecraft.block.Block.class);
		System.out.println(BaseBlock.class.getSuperclass());
		Block block = new BaseBlock(Block.Settings.of(Material.AIR)) {
			@Override
			public boolean hasDynamicBounds() {
				return true;
			}
		};
		System.out.println(block.hasDynamicBounds());
		System.exit(0);
	}
}
