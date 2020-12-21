package io.github.astrarre.testmod;

import io.github.astrarre.internal.gui.GuiScreen;
import io.github.astrarre.internal.gui.GuiScreenHandler;
import io.github.astrarre.testmod.blocks.TestBlock;
import io.github.astrarre.v0.api.util.Validate;
import io.github.astrarre.v0.block.MinecraftMaterials;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Validate.void_(GuiScreenHandler.TYPE);
		Validate.void_(GuiScreen.TEXTURE);
		System.out.println("====================== TEST MOD INIT ======================");
		Registry.register(Registry.BLOCK,
				new Identifier("testmod", "block"),
				(Block) (Object) new TestBlock(io.github.astrarre.v0.block.Block.Settings.of(MinecraftMaterials.STONE)));
	}
}
