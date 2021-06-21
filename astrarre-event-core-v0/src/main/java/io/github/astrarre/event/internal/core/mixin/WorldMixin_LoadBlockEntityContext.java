package io.github.astrarre.event.internal.core.mixin;

import io.github.astrarre.event.internal.core.CopyingContextUtil;
import io.github.astrarre.event.internal.core.InternalContexts;
import io.github.astrarre.event.internal.core.access.ContextProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

@Mixin(World.class)
public class WorldMixin_LoadBlockEntityContext {
	@Redirect (method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/BlockEntityTickInvoker;tick()V"))
	public void onTick(BlockEntityTickInvoker invoker) {
		BlockEntity entity = InternalContexts.from(invoker);
		if(entity != null) {
			CopyingContextUtil.loadContext((ContextProvider) entity);
		}
		invoker.tick();
		if(entity != null) {
			CopyingContextUtil.pop((ContextProvider) entity);
		}
	}
}
