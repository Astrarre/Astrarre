package io.github.astrarre.rendering.internal.mixin;

import java.util.Map;
import java.util.WeakHashMap;

import io.github.astrarre.rendering.internal.BufferData;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.BufferBuilder;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin_BufferDataImpl implements BufferData {
	Map<VertexFormat<?>, Global> cache;

	@Override
	public Map<VertexFormat<?>, Global> astrarre_configCache() {
		var cache = this.cache;
		if(cache == null) {
			this.cache = cache = new WeakHashMap<>();
		}
		return cache;
	}
}
