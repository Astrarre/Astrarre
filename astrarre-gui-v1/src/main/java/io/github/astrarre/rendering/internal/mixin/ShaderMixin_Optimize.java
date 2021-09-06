package io.github.astrarre.rendering.internal.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.Shader;

@Mixin(Shader.class)
public class ShaderMixin_Optimize {
	@Shadow @Final private List<Integer> loadedSamplerIds;

	@Redirect(method = "bind", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/GlUniform;getUniformLocation(ILjava/lang/CharSequence;)I"))
	public int loc(int program, CharSequence name) {
		return -1;
	}

	@Redirect(method = "bind", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/GlUniform;uniform1(II)V"))
	public void onUniformOne(int location, int value) {
		GlUniform.uniform1(this.loadedSamplerIds.get(value), value);
	}
}
