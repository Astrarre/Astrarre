package io.github.astrarre.rendering.internal.mixin;

import io.github.astrarre.rendering.internal.BufferData;
import io.github.astrarre.rendering.internal.ogl.OpenGLRendererImpl;
import io.github.astrarre.rendering.v1.edge.OpenGLRenderer;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.BufferBuilder;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin_BufferDataImpl implements BufferData {
	OpenGLRendererImpl renderer;

	@Override
	public OpenGLRendererImpl getRenderer() {
		OpenGLRendererImpl renderer = this.renderer;
		if(renderer == null) {
			this.renderer = renderer = new OpenGLRendererImpl((BufferBuilder) (Object) this);
		}
		return renderer;
	}
}
