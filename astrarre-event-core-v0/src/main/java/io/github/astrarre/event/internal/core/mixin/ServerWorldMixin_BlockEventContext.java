package io.github.astrarre.event.internal.core.mixin;

import io.github.astrarre.event.internal.core.BlockEventUtil;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerWorld.class)
public class ServerWorldMixin_BlockEventContext {
	@ModifyArg (method = "addSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;add(Ljava/lang/Object;)Z"))
	public Object onSync(Object added) {
		BlockEventUtil.initContext((BlockEvent) added);
		return added;
	}

	@Redirect(method = "processBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onSyncedBlockEvent(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z"))
	public boolean onSynced(BlockState state, World world, BlockPos pos, int type, int data, BlockEvent event) {
		try {
			BlockEventUtil.loadContext(event);
			return state.onSyncedBlockEvent(world, pos, type, data);
		} finally {
			BlockEventUtil.pop(event);
		}
	}
}
