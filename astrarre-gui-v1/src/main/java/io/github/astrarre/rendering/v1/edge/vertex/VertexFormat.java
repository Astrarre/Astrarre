package io.github.astrarre.rendering.v1.edge.vertex;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import io.github.astrarre.rendering.internal.ogl.OpenGLRendererImpl;
import io.github.astrarre.rendering.internal.ogl.VertexFormatImpl;
import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.Image;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
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
 * Store these in a static field or somn, don't keep making them
 *
 * @param <F>
 */
public interface VertexFormat<F extends Global> {
	VertexFormat<Primitive<Pos<Color<End>>>> POS_COLOR = VertexFormatImpl.create(GameRenderer::getPositionColorShader, VertexFormats.POSITION_COLOR);
	VertexFormat<Image<Primitive<Pos<Tex<End>>>>> POS_TEX = VertexFormatImpl.create(GameRenderer::getPositionTexShader, VertexFormats.POSITION_TEXTURE, ShaderSetting.image());
	VertexFormat<Primitive<Pos<Color<Normal<Pad<End>>>>>> LINES = VertexFormatImpl.create(GameRenderer::getRenderTypeLinesShader, VertexFormats.LINES);

	static VertexSettingsBuilder<End> create() {
		return new VertexSettingsBuilder<>();
	}

	/**
	 * must be of type F<\T> and unique per BufferBuilder, cache it or something
	 */
	@ApiStatus.Internal
	F create(OpenGLRendererImpl impl);

	@Edge
	net.minecraft.client.render.VertexFormat asMinecraft();

	void loadShader();

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

		private ShaderSettingsBuilder(Map<String, VertexSetting.Type<?>> vertex) {
			this.vertex = vertex;
		}

		public <A extends ShaderSetting<F>> ShaderSettingsBuilder<A> add(ShaderSetting.Factory<A> type) {
			this.shader.add(type);
			return (ShaderSettingsBuilder<A>) this;
		}

		public VertexFormat<F> build(Identifier shaderId) {
			if(!shaderId.equals(new Identifier(shaderId.getPath()))) {
				throw new UnsupportedOperationException("mod-specific shader ids not yet supported!");
			}
			return new VertexFormatImpl<>(shaderId, this.shader, this.vertex);
		}
	}
}
