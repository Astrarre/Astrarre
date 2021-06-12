package io.github.astrarre.components.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.func.Copier;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ComponentsInternal {
	public static final Identifier SYNC_ENTITY = new Identifier("astrarre-components-v0", "entity_sync"); // int entityId
	public static final Identifier SYNC_PLAYER = new Identifier("astrarre-components-v0", "player_sync"); // int entityId
	public static final Identifier SYNC_BLOCK_ENTITY = new Identifier("astrarre-components-v0", "block_entity_sync"); // int x,y,z

	public static final ComponentManager<BlockEntity> BLOCK_ENTITY_MANAGER = ComponentManager.newManager("astrarre-components-v0", "block_entity");
	public static final ComponentManager<Entity> ENTITY_MANAGER = ComponentManager.newManager("astrarre-components-v0", "entity");
	public static final ComponentManager<PlayerEntity> PLAYER_MANAGER = ComponentManager.newPlayerManager("astrarre-components-v0", "player");

	public static final Map<String, Pair<Component<BlockEntity, ?>, Serializer<?>>> SERIALIZE_BLOCK_ENTITY_INTERNAL = new HashMap<>();
	public static final Map<String, Pair<Component<BlockEntity, ?>, FabricByteSerializer<?>>> SYNC_BLOCK_ENTITY_INTERNAL = new HashMap<>();

	public static final Map<String, Pair<Component<Entity, ?>, Serializer<?>>> SERIALIZE_ENTITY_INTERNAL = new HashMap<>();
	public static final Map<String, Pair<Component<Entity, ?>, FabricByteSerializer<?>>> SYNC_ENTITY_INTERNAL = new HashMap<>();

	public static final List<Pair<Component<Entity, ?>, Copier<?>>> COPY_ENTITY_INTENRAL = new ArrayList<>();


	public static final Map<String, Pair<Component<PlayerEntity, ?>, FabricByteSerializer<?>>> SYNC_PLAYER_INTERNAL = new HashMap<>();
	// copy when moving from end to overworld due to credit scene (?)
	public static final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> COPY_PLAYER_ALIVE = new ArrayList<>();

	// copies on death only if keep inventory is on
	public static final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> COPY_PLAYER_INVENTORY = new ArrayList<>();

	// always copies on death or move dimension
	public static final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> COPY_PLAYER_ALWAYS = new ArrayList<>();

	public static final Map<String, Pair<Component<PlayerEntity, ?>, Serializer<?>>> SERIALIZE_PLAYER_INTERNAL = new HashMap<>();
}
