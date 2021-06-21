package io.github.astrarre.event.internal.core.mixin;

import io.github.astrarre.event.internal.core.CopyingContextUtil;
import io.github.astrarre.event.internal.core.InternalContexts;
import io.github.astrarre.event.internal.core.access.ContextProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

@Mixin(BlockEntity.class)
public class BlockEntityMixin_CopyCreateContext implements ContextProvider {
	private Object astrarre_eventData;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onInit(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
		CopyingContextUtil.initContext(InternalContexts.BLOCK_ENTITY_CREATE, this);
	}

	@Override
	public Object get() {
		return this.astrarre_eventData;
	}

	@Override
	public void set(Object val) {
		this.astrarre_eventData = val;
	}
}
