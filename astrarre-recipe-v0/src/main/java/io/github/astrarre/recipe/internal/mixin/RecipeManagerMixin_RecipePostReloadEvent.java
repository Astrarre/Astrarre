package io.github.astrarre.recipe.internal.mixin;

import java.util.Map;

import com.google.gson.JsonElement;
import io.github.astrarre.recipe.v0.fabric.RecipePostReloadEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin_RecipePostReloadEvent {
	@Shadow private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;

	@Inject(method = "apply", at = @At("RETURN"))
	public void onApply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
		RecipePostReloadEvent.EVENT.get().onReload((RecipeManager) (Object) this, this.recipes);
	}
}
