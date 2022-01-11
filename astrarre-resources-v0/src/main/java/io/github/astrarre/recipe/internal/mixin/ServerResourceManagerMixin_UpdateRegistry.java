package io.github.astrarre.recipe.internal.mixin;

import io.github.astrarre.recipe.internal.ReloadableManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;

@Mixin(ServerResourceManager.class)
public class ServerResourceManagerMixin_UpdateRegistry {
	@Shadow @Final private ReloadableResourceManager resourceManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void onInit(
			DynamicRegistryManager registryManager,
			CommandManager.RegistrationEnvironment commandEnvironment,
			int functionPermissionLevel,
			CallbackInfo ci) {
		this.resourceManager.registerReloader(ReloadableManager.SERVER);
	}
}
