package io.github.astrarre.components.v0.testmod;

import io.github.astrarre.components.v0.api.components.IntComponent;
import io.github.astrarre.components.v0.fabric.EntityComponentBuilder;
import io.github.astrarre.components.v0.fabric.PlayerComponentBuilder;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TestMod implements ModInitializer {
	public static final IntComponent<Entity> INT = EntityComponentBuilder.entity()
	                                                                     .serializePrimitive()
	                                                                     .syncPrimitive()
	                                                                     .buildInt(new Identifier("testmod:test_entity"));
	public static final IntComponent<PlayerEntity> INT_PLAYER = PlayerComponentBuilder.player()
	                                                                                  .serializePrimitive()
	                                                                                  .syncPrimitive()
	                                                                                  .copyInventoryPrimitive()
	                                                                                  .buildInt(new Identifier("testmod:test_player"));

	@Override
	public void onInitialize() {
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			int max = INT.getInt(killedEntity);
			if (entity instanceof PlayerEntity) {
				((PlayerEntity) entity).sendMessage(new LiteralText("serverside " + max), false);
				INT_PLAYER.setInt((PlayerEntity) entity, INT_PLAYER.getInt((PlayerEntity) entity) + 1);
			}
		});
		ServerTickEvents.START_WORLD_TICK.register(world -> {
			for (Entity entity : world.iterateEntities()) {
				INT.setInt(entity, INT.getInt(entity) + 1);
			}
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			System.out.println("old " + INT_PLAYER.getInt(oldPlayer));
			System.out.println("new " + INT_PLAYER.getInt(newPlayer));
		});
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			int max = INT.getInt(entity);
			for (AbstractClientPlayerEntity player : world.getPlayers()) {
				player.sendMessage(new LiteralText("clientside " + max), false);
			}
		});
	}
}
