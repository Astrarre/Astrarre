package io.github.astrarre.rendering.internal.mixin;

import io.github.astrarre.rendering.internal.access.BufferBuilderAccess;
import io.github.astrarre.rendering.internal.ogl.VertexRendererImpl;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.BufferBuilder;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin_AccessImpl implements BufferBuilderAccess {
	VertexRendererImpl renderer;

	@Override
	public VertexRendererImpl getRenderer() {
		VertexRendererImpl renderer = this.renderer;
		if(renderer == null) {
			this.renderer = renderer = new VertexRendererImpl((BufferBuilder) (Object) this);
		}
		return renderer;
	}
}
