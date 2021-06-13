package io.github.astrarre.event.testmod.player;

import io.github.astrarre.event.v0.api.player.PlayerContexts;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("testmod:testblock"), new Block(AbstractBlock.Settings.copy(Blocks.STONE)) {
			@Override
			public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
				System.out.println(PlayerContexts.INTERACT_BLOCK.getFirst());
				return ActionResult.CONSUME;
			}

			@Override
			public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
				System.out.println(PlayerContexts.INTERACT_BLOCK.getFirst());
			}
		});
	}
}
