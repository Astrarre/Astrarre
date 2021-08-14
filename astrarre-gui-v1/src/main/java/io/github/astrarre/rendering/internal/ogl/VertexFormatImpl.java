package io.github.astrarre.rendering.internal.ogl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.mem.DataStack;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;
import io.github.astrarre.rendering.v1.edge.vertex.settings.End;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSettingInternal;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.util.Identifier;

public final class VertexFormatImpl<F extends Global> implements VertexFormat<F> {
	public static final Set<VertexFormatImpl<?>> VERTEX_FORMATS = Collections.newSetFromMap(new WeakHashMap<>());

	public final Supplier<Shader> shader;
	@Nullable
	public final Identifier shaderId;
	public Shader shaderRef;
	final net.minecraft.client.render.VertexFormat format;
	final DataStack stack = new DataStack();
	final List<ShaderSetting.Factory<?>> shaderSettings;
	final Map<String, VertexSetting.Type<?>> vertex;

	public VertexFormatImpl(@NotNull Identifier id, List<ShaderSetting.Factory<?>> shader, Map<String, VertexSetting.Type<?>> vertex) {
		this.shaderId = id;
		VERTEX_FORMATS.add(this);
		this.shader = () -> this.shaderRef;

		var elementMap = ImmutableMap.<String, VertexFormatElement>builder();
		for(Map.Entry<String, VertexSetting.Type<?>> entry : vertex.entrySet()) {
			elementMap.put(entry.getKey(), entry.getValue().element());
		}
		this.format = new net.minecraft.client.render.VertexFormat(elementMap.build());
		this.shaderSettings = shader;
		this.vertex = vertex;
	}

	public VertexFormatImpl(List<ShaderSetting.Factory<?>> shader,
			net.minecraft.client.render.VertexFormat format,
			Supplier<Shader> supplier) {
		this.shader = supplier;
		this.shaderId = null;
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
		this.shaderSettings = shader;
	}

	public static <T extends Global> VertexFormat<T> create(Supplier<Shader> shaderSupplier, net.minecraft.client.render.VertexFormat element,
			ShaderSetting.Factory<?>... factories) {
		return new VertexFormatImpl<>(Arrays.asList(factories), element, shaderSupplier);
	}

	@Override
	public F create(OpenGLRendererImpl impl) {
		VertexSetting<?> current = End.INSTANCE;
		List<VertexSetting.Type<?>> reversed = new ArrayList<>(this.vertex.values());
		for(int i = reversed.size() - 1; i >= 0; i--) {
			current = reversed.get(i).factory().create(impl, this, current);
		}

		PrimitiveSupplierImpl supplier = new PrimitiveSupplierImpl(impl, current);
		Global global = supplier;
		for(ShaderSetting.Factory<?> factory : this.shaderSettings) {
			global = factory.create(global);
		}
		supplier.outest = global;
		return (F) global;
	}

	@Override
	public net.minecraft.client.render.VertexFormat asMinecraft() {
		return this.format;
	}

	@Override
	public void loadShader() {
		RenderSystem.setShader(this.shader);
	}

	private class PrimitiveSupplierImpl implements PrimitiveSupplier {
		private final OpenGLRendererImpl impl;
		private final VertexSetting<?> next;
		private Global outest;

		public PrimitiveSupplierImpl(OpenGLRendererImpl impl, VertexSetting<?> next) {
			this.impl = impl;
			this.next = next;
		}

		@Override
		public PrimitiveImpl<?> create() {
			BuiltDataStack stack = VertexFormatImpl.this.stack.build();
			PrimitiveImpl primitive = new PrimitiveImpl<>(this.impl, VertexFormatImpl.this, this.next, stack);
			primitive.outest = this.outest;
			return primitive;
		}

		@Override
		public DataStack getActive() {
			return VertexFormatImpl.this.stack;
		}
	}
}
