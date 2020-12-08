package test;

import v0.io.github.astrarre.IBootstrap;
import v0.io.github.astrarre.block.BaseBlock;
import v0.io.github.astrarre.block.IBlock;
import v0.io.github.astrarre.block.Materials;

import net.minecraft.block.Block;

import net.fabricmc.api.ModInitializer;

public class Cast implements ModInitializer {
	@Override
	public void onInitialize() {
		IBlock block = new BaseBlock(IBlock.Settings.of(Materials.AIR));
		System.out.println(((Block)block).hasBlockEntity());
	}
}
