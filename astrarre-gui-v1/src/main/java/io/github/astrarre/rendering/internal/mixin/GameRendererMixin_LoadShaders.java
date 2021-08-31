package io.github.astrarre.rendering.internal.mixin;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.rendering.internal.ogl.RenderLayerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gl.Program;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceManager;

@Mixin(GameRenderer.class)
public class GameRendererMixin_LoadShaders {
	@Inject(method = "loadShaders", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onLoad(ResourceManager manager, CallbackInfo ci, List<Program> programs, List<Pair<Shader, Consumer<Shader>>> shaders)
			throws IOException {
		for(RenderLayerImpl<?> format : RenderLayerImpl.VERTEX_FORMATS) {
			shaders.add(Pair.of(new Shader(manager, format.shaderId.getPath(), format.asMinecraft()), (shader) -> format.shaderRef = shader));
		}
	}
}
