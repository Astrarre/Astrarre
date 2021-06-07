package io.github.astrarre.components.v0.testmod;

import io.github.astrarre.components.v0.api.components.IntComponent;
import io.github.astrarre.components.v0.fabric.builder.BlockEntityComponentBuilder;
import io.github.astrarre.components.v0.fabric.builder.EntityComponentBuilder;
import io.github.astrarre.components.v0.fabric.builder.PlayerComponentBuilder;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TestMod implements ModInitializer {
	public static final IntComponent<Entity> INT_ENTITY = EntityComponentBuilder.entity()
	                                                                            .serializePrimitive()
	                                                                            .syncPrimitive()
	                                                                            .buildInt(new Identifier("testmod:test_entity"));
	public static final IntComponent<PlayerEntity> INT_PLAYER = PlayerComponentBuilder.player()
	                                                                                  .serializePrimitive()
	                                                                                  .syncPrimitive()
	                                                                                  .copyInventoryPrimitive()
	                                                                                  .buildInt(new Identifier("testmod:test_player"));
	public static final IntComponent<BlockEntity> INT_BLOCK_ENTITY = BlockEntityComponentBuilder.blockEntity()
	                                                                                            .serializePrimitive()
	                                                                                            .syncPrimitive()
	                                                                                            .buildInt(new Identifier("testmod:test_block_entity"
	                                                                                            ));

	@Override
	public void onInitialize() {
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			INT_BLOCK_ENTITY.setInt(blockEntity, INT_BLOCK_ENTITY.getInt(blockEntity) + 1);
		});
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			System.out.println("server " +INT_BLOCK_ENTITY.getInt(blockEntity));
		});
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			System.out.println("cleint " + INT_BLOCK_ENTITY.getInt(blockEntity));
		});

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			int max = INT_ENTITY.getInt(killedEntity);
			if (entity instanceof PlayerEntity) {
				((PlayerEntity) entity).sendMessage(new LiteralText("serverside " + max), false);
				INT_PLAYER.setInt((PlayerEntity) entity, INT_PLAYER.getInt((PlayerEntity) entity) + 1);
			}
		});
		ServerTickEvents.START_WORLD_TICK.register(world -> {
			for (Entity entity : world.iterateEntities()) {
				INT_ENTITY.setInt(entity, INT_ENTITY.getInt(entity) + 1);
			}
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			System.out.println("old " + INT_PLAYER.getInt(oldPlayer));
			System.out.println("new " + INT_PLAYER.getInt(newPlayer));
		});
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			int max = INT_ENTITY.getInt(entity);
			for (AbstractClientPlayerEntity player : world.getPlayers()) {
				player.sendMessage(new LiteralText("clientside " + max), false);
			}
		});
	}
}
