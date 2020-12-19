package io.github.astrarre.testmod.blocks;

import v0.io.github.astrarre.block.BaseBlock;
import v0.io.github.astrarre.block.MinecraftBlocks;
import v0.io.github.astrarre.entity.Entity;
import v0.io.github.astrarre.util.math.BlockPos;
import v0.io.github.astrarre.world.World;

public class TestBlock extends BaseBlock {
	public TestBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void onSteppedOn(World arg0, BlockPos arg1, Entity arg2) {
		arg0.setBlockState(arg1, MinecraftBlocks.AIR.getDefaultState());
	}
}
