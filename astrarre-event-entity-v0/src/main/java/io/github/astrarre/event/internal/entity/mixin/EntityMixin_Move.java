package io.github.astrarre.event.internal.entity.mixin;

import io.github.astrarre.event.v0.fabric.entity.EntityContexts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

@Mixin(value = Entity.class, priority = 10000)
public abstract class EntityMixin_Move {

	@Inject(method = "move", at = @At("HEAD"))
	public void onMove(MovementType movementType, Vec3d movement, CallbackInfo ci) {
		EntityContexts.MOVE_ENTITY.push((Entity) (Object) this);
	}

	@Inject(method = "move", at = @At("RETURN"))
	public void onMovePop(MovementType movementType, Vec3d movement, CallbackInfo ci) {
		EntityContexts.MOVE_ENTITY.pop((Entity) (Object) this);
	}
}
