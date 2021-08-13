package io.github.astrarre.rendering.internal.ogl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.astrarre.rendering.internal.BufferAccess;
import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;
import io.github.astrarre.rendering.v1.edge.vertex.settings.End;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSettingInternal;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormatElement;

public final class VertexFormatImpl<F extends Global> implements VertexFormat<F> {
	private final List<ShaderSetting.Factory<?>> shader;
	private final Map<String, VertexSetting.Type<?>> vertex;
	final net.minecraft.client.render.VertexFormat format;

	public VertexFormatImpl(List<ShaderSetting.Factory<?>> shader, Map<String, VertexSetting.Type<?>> vertex) {
		var elementMap = ImmutableMap.<String, VertexFormatElement>builder();
		for(Map.Entry<String, VertexSetting.Type<?>> entry : vertex.entrySet()) {
			elementMap.put(entry.getKey(), entry.getValue().element());
		}
		this.format = new net.minecraft.client.render.VertexFormat(elementMap.build());
		this.shader = shader;
		this.vertex = vertex;
	}

	public VertexFormatImpl(List<ShaderSetting.Factory<?>> shader,
			Map<String, VertexSetting.Type<?>> vertex,
			net.minecraft.client.render.VertexFormat format) {
		this.shader = shader;
		this.vertex = vertex;
		this.format = format;
	}

	public VertexFormatImpl(List<ShaderSetting.Factory<?>> shader, net.minecraft.client.render.VertexFormat format) {
		ImmutableList<String> names = format.getShaderAttributes();
		ImmutableList<VertexFormatElement> elements = format.getElements();
		Map<String, VertexSetting.Type<?>> vertex = new HashMap<>();
		for(int i = 0; i < names.size(); i++) {
			String attribute = names.get(i);
			VertexFormatElement element = elements.get(i);
			VertexSetting.Type<?> type = VertexSettingInternal.DEFAULT_IMPL.get(element);
			Validate.notNull(type, "No default implementation of " + element);
			vertex.put(attribute, type);
		}
		this.vertex = vertex;
		this.format = format;
		this.shader = shader;
	}

	public static <T extends Global> VertexFormat<T> create(net.minecraft.client.render.VertexFormat element, ShaderSetting.Factory<?>... factories) {
		return new VertexFormatImpl<>(Arrays.asList(factories), element);
	}

	@Override
	public F create(OpenGLRendererImpl impl) {
		VertexSetting<?> current = End.INSTANCE;
		for(VertexSetting.Type<?> type : this.vertex.values()) {
			current = type.factory().create(impl, this, current);
		}

		var prim = new PrimitiveImpl<>(impl, this, current);
		Global global = prim;
		for(ShaderSetting.Factory<?> factory : this.shader) {
			global = factory.create(global);
		}
		prim.outest = global;
		return (F) global;
	}

	@Override
	public net.minecraft.client.render.VertexFormat asMinecraft() {
		return this.format;
	}
}
