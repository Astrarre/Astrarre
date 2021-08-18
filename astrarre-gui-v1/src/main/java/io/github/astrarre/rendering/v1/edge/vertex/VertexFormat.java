package io.github.astrarre.rendering.v1.edge.vertex;

import static io.github.astrarre.rendering.internal.ogl.VertexFormatImpl.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import io.github.astrarre.rendering.internal.ogl.VertexFormatImpl;
import io.github.astrarre.rendering.internal.ogl.VertexRendererImpl;
import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.setting.Img;
import io.github.astrarre.rendering.v1.edge.shader.setting.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.settings.Color;
import io.github.astrarre.rendering.v1.edge.vertex.settings.End;
import io.github.astrarre.rendering.v1.edge.vertex.settings.Normal;
import io.github.astrarre.rendering.v1.edge.vertex.settings.Pad;
import io.github.astrarre.rendering.v1.edge.vertex.settings.Pos;
import io.github.astrarre.rendering.v1.edge.vertex.settings.Tex;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;
import io.github.astrarre.util.v0.api.Edge;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

/**
 * Store these in a static field or somn, don't keep making them.
 */
@SuppressWarnings("unchecked")
public interface VertexFormat<F extends Global> {
	VertexFormat<Primitive<Pos<Color<End>>>> POS_COLOR = impl(VertexFormats.POSITION_COLOR).ext(RenderPhases.NO_TEXTURE)
			                                                     .build(GameRenderer::getPositionColorShader);
	VertexFormat<Img<Primitive<Pos<Tex<End>>>>> POS_TEX = impl(VertexFormats.POSITION_TEXTURE).add(ShaderSetting.image())
			                                                      .build(GameRenderer::getPositionTexShader);
	VertexFormat<Primitive<Pos<Color<Normal<Pad<End>>>>>> LINES = impl(VertexFormats.LINES).build(GameRenderer::getRenderTypeLinesShader);


	static VertexSettingsBuilder<End> builder() {
		return new VertexSettingsBuilder<>();
	}

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

		public VertexFormat<F> build(Identifier shaderId) {
			if(!shaderId.equals(new Identifier(shaderId.getPath()))) {
				throw new UnsupportedOperationException("mod-specific shader ids not yet supported!");
			}
			RenderPhase extension;
			if(this.extensions == null) {
				extension = RenderPhase.EMPTY;
			} else {
				extension = RenderPhase.COMBINE.combine(this.extensions.toArray(RenderPhase[]::new));
			}
			return new VertexFormatImpl<>(extension, shaderId, this.shader, this.vertex);
		}
	}
}
