package io.github.astrarre.testmod;

import io.github.astrarre.testmod.blocks.TestBlock;
import v0.io.github.astrarre.block.MinecraftMaterials;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println("====================== TEST MOD INIT ======================");
		Registry.register(Registry.BLOCK,
				new Identifier("testmod", "block"),
				(Block) (Object) new TestBlock(v0.io.github.astrarre.block.Block.Settings.of(MinecraftMaterials.STONE)));
	}
}
