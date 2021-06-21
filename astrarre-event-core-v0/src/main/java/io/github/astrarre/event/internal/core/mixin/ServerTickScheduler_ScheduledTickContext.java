package io.github.astrarre.event.internal.core.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import io.github.astrarre.event.internal.core.CopyingContextUtil;
import io.github.astrarre.event.internal.core.InternalContexts;
import io.github.astrarre.event.internal.core.access.ContextProvider;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;

@Mixin(ServerTickScheduler.class)
public class ServerTickScheduler_ScheduledTickContext {
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
	public void onAccept(Consumer consumer, Object t) {
		try {
			CopyingContextUtil.loadContext((ContextProvider) t);
			consumer.accept(t);
		} finally {
			CopyingContextUtil.pop((ContextProvider) t);
		}
	}

	@ModifyArg (method = "schedule", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;addScheduledTick(Lnet/minecraft/world/ScheduledTick;)V"))
	public ScheduledTick<?> onSchedule(ScheduledTick<?> tick) {
		CopyingContextUtil.initContext(InternalContexts.COPY_SCHEDULED, (ContextProvider) tick);
		return tick;
	}

	private static final ThreadLocal<Object> CONTEXT_OBJECTS = new ThreadLocal<>();
	@Inject(method = "copyScheduledTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ScheduledTick;getObject()Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void copy(BlockBox box, BlockPos offset, CallbackInfo ci, List<?> l, Iterator<?> i, ScheduledTick<?> tick) {
		CONTEXT_OBJECTS.set(((ContextProvider)tick).get());
	}

	@ModifyArg (method = "copyScheduledTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;addScheduledTick(Lnet/minecraft/world/ScheduledTick;)V"))
	public ScheduledTick<?> onSchedule2(ScheduledTick<?> tick) {
		((ContextProvider) tick).set(CONTEXT_OBJECTS.get());
		CONTEXT_OBJECTS.remove();
		return tick;
	}
}
