package io.github.astrarre.rendering.v1.edge.vertex;

import static io.github.astrarre.rendering.internal.ogl.RenderLayerImpl.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import io.github.astrarre.rendering.internal.ogl.RenderLayerImpl;
import io.github.astrarre.rendering.internal.ogl.VertexRendererImpl;
import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.settings.Img;
import io.github.astrarre.rendering.v1.edge.vertex.settings.*;
import io.github.astrarre.rendering.v1.edge.shader.settings.ShaderSetting;
import io.github.astrarre.util.v0.api.Edge;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

@SuppressWarnings("unchecked")
public interface RenderLayer<F extends Global> {
	// rn we need to split Primitive into multiple interfaces, then this will be fully equivalent (ish)
	// you see, render layers are restricted to specific primitives, which is dumb but whatever I guess

	// then we can mix into BufferBuilderStorage for optimizations and stuff if we want I guess

	RenderLayer<Primitive<Pos<Color<End>>>> POS_COLOR = impl(VertexFormats.POSITION_COLOR).ext(RenderPhases.NO_TEXTURE)
			                                                    .build(GameRenderer::getPositionColorShader);
	RenderLayer<Img<Primitive<Pos<Tex<End>>>>> POS_TEX = impl(VertexFormats.POSITION_TEXTURE).add(ShaderSetting.image())
			                                                     .build(GameRenderer::getPositionTexShader);
	RenderLayer<Primitive<Pos<Color<Normal<End>>>>> LINES = impl(VertexFormats.LINES).build(GameRenderer::getRenderTypeLinesShader);

	static VertexSettingsBuilder<End> builder() {
		return new VertexSettingsBuilder<>();
	}

	// for instanced
	// static shader data
	// static per instance data (eg. model part vbo)
	// per instance data (eg. transformation matrix)
	// static per model part data (eg. current vbo, or relative transformation (though that can be done on CPU))
	// per model part data (eg. transformation matrix)
	// static vertex data (eg. everything)
	// per vertex data (special shit)

	/**
	 * must be of type F<\T> and unique per BufferBuilder, cache it or something
	 */
	@ApiStatus.Internal
	F create(VertexRendererImpl impl);

	@Edge
	net.minecraft.client.render.VertexFormat asMinecraft();

	@ApiStatus.Internal
	void loadShader();

	@ApiStatus.Internal
	void takedownShader();

	// todo instanced rendering api proposal
	// Type<Color<?>> color = Color.TYPE;
	// Type<Pos<?>> pos = Pos.TYPE;
	// RenderLayer<Pos<Color<End>>> layer = builder().add(color.as()).add(pos.as()).build();
	// then, color/pos could be used as variables in the ast generation phase
	// maybe we should use a "shaderbuilder" instead of renderlayer
	// as in this case would handle casting

	// for instanced rendering, make 2 parallel linked lists of generics
	// the builder atm uses 1 linked list of generics
	// each vertex setting type would carry 2 generics, one is the vertex setting and the other is the vertex field type
	// the vertex field type could then be used when generating the shader

	class VertexSettingsBuilder<F extends VertexSetting<?>> {
		final Map<String, VertexSetting.Type<?>> vertex = new LinkedHashMap<>(); // order must be preserved

		private VertexSettingsBuilder() {
		}

		public <A extends VertexSetting<F>> VertexSettingsBuilder<A> add(String name, VertexSetting.Type<A> type) {
			this.vertex.put(name, type);
			return (VertexSettingsBuilder<A>) this;
		}

		public ShaderSettingsBuilder<Primitive<F>> next() {
			return new ShaderSettingsBuilder<>(ImmutableMap.copyOf(this.vertex));
		}

	}

	class ShaderSettingsBuilder<F extends Global> {
		final List<ShaderSetting.Factory<?>> shader = new ArrayList<>();
		final Map<String, VertexSetting.Type<?>> vertex;
		List<RenderPhase> extensions;

		private ShaderSettingsBuilder(Map<String, VertexSetting.Type<?>> vertex) {
			this.vertex = vertex;
		}

		public <A extends ShaderSetting<F>> ShaderSettingsBuilder<A> add(ShaderSetting.Factory<A> type) {
			this.shader.add(type);
			return (ShaderSettingsBuilder<A>) this;
		}

		public ShaderSettingsBuilder<F> ext(RenderPhase extension) {
			if(this.extensions == null) {
				this.extensions = new ArrayList<>();
			}
			this.extensions.add(extension);
			return this;
		}

		public RenderLayer<F> build(Identifier shaderId) {
			if(!shaderId.equals(new Identifier(shaderId.getPath()))) {
				throw new UnsupportedOperationException("mod-specific shader ids not yet supported!");
			}
			RenderPhase extension;
			if(this.extensions == null) {
				extension = RenderPhase.EMPTY;
			} else {
				extension = RenderPhase.COMBINE.combine(this.extensions.toArray(RenderPhase[]::new));
			}
			return new RenderLayerImpl<>(extension, shaderId, this.shader, this.vertex);
		}
	}
}
