package io.github.astrarre.gui.internal.mixin;

import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import io.github.astrarre.gui.internal.access.ServerPlayerEntityAccess;
import io.github.astrarre.gui.internal.access.TickingPanel;
import io.github.astrarre.gui.internal.comms.AbstractComms;
import io.github.astrarre.hash.v0.api.HashKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin_Access extends PlayerEntity implements ServerPlayerEntityAccess {
	final Map<HashKey, AbstractComms.Server> astrarre_coms = new HashMap<>();

	public ServerPlayerEntityMixin_Access(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Override
	public Map<HashKey, AbstractComms.Server> astrarre_coms() {
		return this.astrarre_coms;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void onTick(CallbackInfo ci) {
		for(AbstractComms.Server value : this.astrarre_coms.values()) {
			value.startQueue();
		}
		((TickingPanel)this.currentScreenHandler).astrarre_tick();
		for(AbstractComms.Server value : this.astrarre_coms.values()) {
			value.flushQueue();
		}
	}
}
