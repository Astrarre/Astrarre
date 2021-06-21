package io.github.astrarre.event.internal.entity.mixin;

import io.github.astrarre.event.v0.fabric.entity.EntityContexts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

@Mixin (value = ServerWorld.class, priority = 10000)
public class ServerWorldMixin_EntityTick {
	@Inject (method = "tickEntity",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/util/function/Supplier;)V"))
	public void onTick(Entity entity, CallbackInfo ci) {
		EntityContexts.TICK_ENTITY.push(entity);
	}

	@Inject (method = "tickEntity", at = @At (value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
	public void onTickPop(Entity entity, CallbackInfo ci) {
		EntityContexts.TICK_ENTITY.pop(entity);
	}

	@Inject(method = "tickPassenger", at = @At("HEAD"))
	public void onTickPassenger(Entity vehicle, Entity passenger, CallbackInfo ci) {
		EntityContexts.TICK_ENTITY.push(passenger);
	}

	@Inject(method = "tickPassenger", at = @At("RETURN"))
	public void onTickPassengerPop(Entity vehicle, Entity passenger, CallbackInfo ci) {
		EntityContexts.TICK_ENTITY.pop(passenger);
	}
}
