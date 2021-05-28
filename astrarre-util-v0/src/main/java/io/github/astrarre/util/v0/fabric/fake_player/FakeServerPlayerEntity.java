package io.github.astrarre.util.v0.fabric.fake_player;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class FakeServerPlayerEntity extends ServerPlayerEntity {
	private static final UUID FAKE_PLAYER_UUID = new UUID(-1L, -1L);
	public FakeServerPlayerEntity(MinecraftServer server, ServerWorld world, Vec3d pos) {
		super(server, world, new GameProfile(FAKE_PLAYER_UUID, "indrev_fake_player"));
		this.setPos(pos.x, pos.y, pos.z);
		this.networkHandler = new FakeServerPlayNetworkHandler(server, this);
	}

	@Override
	public void tick() {
	}

	@Override
	public void playSound(SoundEvent sound, float volume, float pitch) {
	}

	@Override
	public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	@Override
	public boolean isSpectator() {
		return false;
	}

	@Override
	public boolean isCreative() {
		return false;
	}
}
