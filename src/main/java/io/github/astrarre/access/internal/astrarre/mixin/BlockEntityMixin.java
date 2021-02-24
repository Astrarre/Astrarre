package io.github.astrarre.access.internal.astrarre.mixin;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.astrarre.access.internal.astrarre.access.BlockEntityAccess;
import org.jetbrains.annotations.Nullable;
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
	@Shadow protected BlockPos pos;
	@Unique private Consumer<BlockEntity> astrarre_listener;

	@Override
	public void astrarre_addRemoveOrMoveListener(Consumer<BlockEntity> consumer) {
		if (this.astrarre_listener == null) {
			this.astrarre_listener = consumer;
		} else {
			this.astrarre_listener = this.astrarre_listener.andThen(consumer);
		}
	}

	@Override
	public void astrarre_invalidate() {
		if (this.astrarre_listener != null) {
			this.astrarre_listener.accept((BlockEntity) (Object) this);
			this.astrarre_listener = null;
		}
	}

	@Inject (method = "setLocation", at = @At ("HEAD"))
	public void onMove(World world, BlockPos pos, CallbackInfo ci) {
		if (this.astrarre_listener != null && !(world == this.world && Objects.equals(pos, this.pos))) {
			this.astrarre_listener.accept((BlockEntity) (Object) this);
			this.astrarre_listener = null;
		}
	}

	@Inject (method = "setPos", at = @At ("HEAD"))
	public void onMove(BlockPos pos, CallbackInfo ci) {
		if (this.astrarre_listener != null && !(Objects.equals(pos, this.pos))) {
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
