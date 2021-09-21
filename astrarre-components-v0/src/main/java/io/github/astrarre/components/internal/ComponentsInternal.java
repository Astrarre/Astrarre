package io.github.astrarre.components.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.github.astrarre.components.v0.fabric.builder.BlockEntityComponentBuilder;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.func.Copier;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class ComponentsInternal {
	public static final Identifier SYNC_ENTITY = new Identifier("astrarre-components-v0", "entity_sync"); // int entityId
	public static final Identifier SYNC_PLAYER = new Identifier("astrarre-components-v0", "player_sync"); // int entityId
	public static final Identifier SYNC_CHUNK = new Identifier("astrarre-components-v0", "chunk_sync"); // world id, x, y
	public static final Identifier SYNC_WORLD = new Identifier("astrarre-components-v0", "chunk_sync"); // world id
	// todo ClientChunk

	public static final Identifier SYNC_BLOCK_ENTITY = new Identifier("astrarre-components-v0", "block_entity_sync"); // int x,y,z

	public static final ComponentManager<BlockEntity> BLOCK_ENTITY_MANAGER = ComponentManager.newManager("astrarre-components-v0", "block_entity");
	public static final ComponentManager<Entity> ENTITY_MANAGER = ComponentManager.newManager("astrarre-components-v0", "entity");
	public static final ComponentManager<WorldChunk> CHUNK_MANAGER = ComponentManager.newManager("astrarre-components-v0", "chunk");
	public static final ComponentManager<PlayerEntity> PLAYER_MANAGER = ComponentManager.newPlayerManager("astrarre-components-v0", "player");

	public static final Map<String, Pair<Component<BlockEntity, ?>, Serializer<?>>> SERIALIZE_BLOCK_ENTITY_INTERNAL = new HashMap<>();
	public static final Map<String, Pair<Component<BlockEntity, ?>, FabricByteSerializer<?>>> SYNC_BLOCK_ENTITY_INTERNAL = new HashMap<>();

	public static final Map<String, Pair<Component<Entity, ?>, Serializer<?>>> SERIALIZE_ENTITY_INTERNAL = new HashMap<>();
	public static final Map<String, Pair<Component<Entity, ?>, FabricByteSerializer<?>>> SYNC_ENTITY_INTERNAL = new HashMap<>();

	public static final List<Pair<Component<Entity, ?>, Copier<?>>> COPY_ENTITY_INTENRAL = new ArrayList<>();

	public static final Map<String, Pair<Component<WorldChunk, ?>, Serializer<?>>> SERIALIZE_CHUNK_INTERNAL = new HashMap<>();
	public static final Map<String, Pair<Component<WorldChunk, ?>, FabricByteSerializer<?>>> SYNC_CHUNK_INTERNAL = new HashMap<>();

	public static final Map<String, Pair<Component<PlayerEntity, ?>, FabricByteSerializer<?>>> SYNC_PLAYER_INTERNAL = new HashMap<>();
	// copy when moving from end to overworld due to credit scene (?)
	public static final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> COPY_PLAYER_ALIVE = new ArrayList<>();

	// copies on death only if keep inventory is on
	public static final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> COPY_PLAYER_INVENTORY = new ArrayList<>();

	// always copies on death or move dimension
	public static final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> COPY_PLAYER_ALWAYS = new ArrayList<>();

	public static final Map<String, Pair<Component<PlayerEntity, ?>, Serializer<?>>> SERIALIZE_PLAYER_INTERNAL = new HashMap<>();

	public static boolean deserializeBlockEntity(PacketByteBuf buf, @Nullable WorldChunk worldChunk) throws IOException {
		World world = astrarre_validateWorld(buf);
		String registryEntry = buf.readString();
		BlockPos position = buf.readBlockPos();
		BlockEntity entity = worldChunk == null ? world.getBlockEntity(position) : worldChunk.getBlockEntity(position);
		if(entity != null && registryEntry.equals(Registry.BLOCK_ENTITY_TYPE.getId(entity.getType()).toString())) {
			return extracted(SYNC_BLOCK_ENTITY_INTERNAL, buf, entity);
		}
		return false;
	}

	public static boolean deserializeChunk(PacketByteBuf buf, @Nullable WorldChunk chunk) throws IOException {
		World currentWorld = astrarre_validateWorld(buf);
		if(currentWorld != null) {
			ChunkPos position = buf.readChunkPos();
			WorldChunk current = chunk == null ? currentWorld.getChunk(position.x, position.z) : chunk;
			if(current != null) {
				return extracted(SYNC_CHUNK_INTERNAL, buf, current);
			}
		}
		return false;
	}

	public static boolean deserializePlayer(PacketByteBuf buf) throws IOException {
		World currentWorld = astrarre_validateWorld(buf);
		if(currentWorld != null) {
			Entity entity = currentWorld.getEntityById(buf.readInt());
			if(entity instanceof PlayerEntity p) {
				return extracted(SYNC_PLAYER_INTERNAL, buf, p);
			}
		}
		return false;
	}

	public static boolean deserializeEntity(PacketByteBuf buf) throws IOException {
		World currentWorld = astrarre_validateWorld(buf);
		if(currentWorld != null) {
			Entity entity = currentWorld.getEntityById(buf.readInt());
			return extracted(SYNC_ENTITY_INTERNAL, buf, entity);
		}
		return false;
	}

	public static <C> NbtCompound write(C context, Map<String, Pair<Component<C, ?>, Serializer<?>>> map) {
		NbtCompound componentData = new NbtCompound();
		for (var entry : map.entrySet()) {
			var value = entry.getValue();
			FabricComponents.serialize(componentData, entry.getKey(), context, (Component)value.getFirst(), value.getSecond());
		}
		return componentData;
	}

	public static <C> void read(NbtCompound compound, C context, Map<String, Pair<Component<C, ?>, Serializer<?>>> map) {
		for (String key : compound.getKeys()) {
			var pair = map.get(key);
			if(pair != null) {
				FabricComponents.deserialize(compound.get(key), context, (Component) pair.getFirst(), (Serializer) pair.getSecond());
			}
			// name changed, perhaps some DFU stuff later(?)
		}
	}


	private static <T> boolean extracted(Map<String, Pair<Component<T, ?>, FabricByteSerializer<?>>> map, PacketByteBuf buf, T current)
			throws IOException {
		String key = buf.readString();
		var pair = map.get(key);
		if(pair != null) {
			FabricComponents.deserialize(buf, current, (Component) pair.getFirst(), pair.getSecond());
			return true;
		}
		return false;
	}

	@Nullable
	static World astrarre_validateWorld(PacketByteBuf buf) {
		Identifier worldId = buf.readIdentifier();
		World currentWorld = MinecraftClient.getInstance().world;
		if(currentWorld != null && worldId.equals(currentWorld.getRegistryKey().getValue())) {
			return currentWorld;
		} else {
			return null;
		}
	}
}
