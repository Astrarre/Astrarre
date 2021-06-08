package io.github.astrarre.access.internal.mixin;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.astrarre.access.internal.access.BlockEntityAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin (BlockEntity.class)
public class BlockEntityMixin implements BlockEntityAccess {
	@Shadow @Nullable protected World world;
	@Final @Shadow protected BlockPos pos;
	@Unique private Consumer<BlockEntity> astrarre_listener;

	@Override
	public void astrarre_addRemoveOrMoveListener(Consumer<BlockEntity> consumer) {
		if (this.astrarre_listener == null) {
			this.astrarre_listener = consumer;
		} else {
			this.astrarre_listener = this.astrarre_listener.andThen(consumer);
		}
	}


	@Inject (method = "setWorld", at = @At ("HEAD"))
	public void onMove(World world, CallbackInfo ci) {
		if (this.astrarre_listener != null && !(world == this.world)) {
			this.astrarre_listener.accept((BlockEntity) (Object) this);
			this.astrarre_listener = null;
		}
	}

	@Inject(method = "markRemoved", at = @At("HEAD"))
	public void onRemoved(CallbackInfo ci) {
		if (this.astrarre_listener != null) {
			this.astrarre_listener.accept((BlockEntity) (Object) this);
			this.astrarre_listener = null;
		}
	}
}
