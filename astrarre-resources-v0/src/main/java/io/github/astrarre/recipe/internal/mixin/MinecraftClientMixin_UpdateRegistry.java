package io.github.astrarre.recipe.internal.mixin;

import io.github.astrarre.recipe.internal.ReloadableManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceReloader;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin_UpdateRegistry {
	@Shadow @Final private ReloadableResourceManager resourceManager;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resource/ResourceReload;"))
	public void reload(RunArgs args, CallbackInfo ci) {
		this.resourceManager.registerReloader(ReloadableManager.ClientHolder.CLIENT);
	}
}
