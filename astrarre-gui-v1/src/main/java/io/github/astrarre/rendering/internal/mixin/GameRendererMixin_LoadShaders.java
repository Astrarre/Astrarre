package io.github.astrarre.rendering.internal.mixin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.rendering.internal.ogl.VertexFormatImpl;
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
	@Inject(method = "loadShaders", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onLoad(ResourceManager manager, CallbackInfo ci, List<Program> programs, ArrayList<Pair<Shader, Consumer<Shader>>> shaders)
			throws IOException {
		for(VertexFormatImpl<?> format : VertexFormatImpl.VERTEX_FORMATS) {
			shaders.add(Pair.of(new Shader(manager, format.shaderId.getNamespace(), format.asMinecraft()), (shader) -> format.shaderRef = shader));
		}
	}
}
