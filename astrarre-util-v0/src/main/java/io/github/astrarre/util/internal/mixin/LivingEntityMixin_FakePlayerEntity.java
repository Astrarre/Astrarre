package io.github.astrarre.util.internal.mixin;

import io.github.astrarre.util.v0.fabric.fake_player.FakeServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public class LivingEntityMixin_FakePlayerEntity {
	@Inject (method = "drop", at = @At ("INVOKE"), cancellable = true)
	public void cancelDrop(DamageSource source, CallbackInfo ci) {
		if (source.getAttacker() instanceof FakeServerPlayerEntity) {
			ci.cancel();
		}
	}
}
