package io.github.astrarre.rendering.internal.ogl;

import java.util.ArrayList;
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
import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.mem.DataStack;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.settings.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.RenderLayer;
import io.github.astrarre.rendering.v1.edge.vertex.RenderPhase;
import io.github.astrarre.rendering.v1.edge.vertex.settings.End;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSettingInternal;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.util.Identifier;

public final class RenderLayerImpl<F extends Global> implements RenderLayer<F> {
	public static final Set<RenderLayerImpl<?>> VERTEX_FORMATS = Collections.newSetFromMap(new WeakHashMap<>());

	public final Supplier<net.minecraft.client.render.Shader> shader;
	public final RenderPhase extension;
	@Nullable public final Identifier shaderId;
	final net.minecraft.client.render.VertexFormat format;
	final DataStack stack = new DataStack();
	final List<ShaderSetting.Factory<?>> shaderSettings;
	final Map<String, VertexSetting.Type<?>> vertex;
	public net.minecraft.client.render.Shader shaderRef;

	public RenderLayerImpl(RenderPhase extension,
			@NotNull Identifier id,
			List<ShaderSetting.Factory<?>> shader,
			Map<String, VertexSetting.Type<?>> vertex) {
		this.extension = extension;
		this.shaderId = id;
		VERTEX_FORMATS.add(this);
		this.shader = () -> this.shaderRef;

		var elementMap = ImmutableMap.<String, VertexFormatElement>builder();
		for(Map.Entry<String, VertexSetting.Type<?>> entry : vertex.entrySet()) {
			elementMap.put(entry.getKey(), entry.getValue().element());
		}
		this.format = new net.minecraft.client.render.VertexFormat(elementMap.build());
		this.shaderSettings = ImmutableList.copyOf(shader);
		this.vertex = ImmutableMap.copyOf(vertex);
	}

	public RenderLayerImpl(List<ShaderSetting.Factory<?>> shader,
			net.minecraft.client.render.RenderLayer layer) {
		this(shader, layer.getVertexFormat(), null, new RenderPhase() {
			@Override
			public void init() {
				layer.startDrawing();
			}

			@Override
			public void takedown() {
				layer.endDrawing();
			}
		});
	}


	public RenderLayerImpl(List<ShaderSetting.Factory<?>> shader,
			net.minecraft.client.render.VertexFormat format,
			Supplier<net.minecraft.client.render.Shader> supplier,
			RenderPhase extension) {
		this.shader = supplier;
		this.extension = extension;
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

	public static <T extends VertexSetting<?>> RBuilder<Primitive<T>> impl(net.minecraft.client.render.RenderLayer format) {
		return new RBuilder<>(format);
	}

	public static <T extends VertexSetting<?>> VBuilder<Primitive<T>> impl(net.minecraft.client.render.VertexFormat format) {
		return new VBuilder<>(format);
	}

	@Override
	public F create(VertexRendererImpl impl) {
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
		if(this.shader != null) {
			RenderSystem.setShader(this.shader);
		}
		this.extension.init();
	}

	@Override
	public void takedownShader() {
		this.extension.takedown();
	}

	public static final class RBuilder<F extends Global> {
		final net.minecraft.client.render.RenderLayer layer;
		final List<ShaderSetting.Factory<?>> factories = new ArrayList<>();

		public RBuilder(net.minecraft.client.render.RenderLayer layer) {
			this.layer = layer;
		}

		public <A extends ShaderSetting<F>> RBuilder<A> add(ShaderSetting.Factory<A> type) {
			this.factories.add(type);
			return (RBuilder<A>) this;
		}

		public RenderLayer build() {
			return new RenderLayerImpl<>(this.factories, this.layer);
		}
	}

	public static final class VBuilder<F extends Global> {
		final List<ShaderSetting.Factory<?>> factories = new ArrayList<>();
		final net.minecraft.client.render.VertexFormat format;
		List<RenderPhase> extensions;


		private VBuilder(net.minecraft.client.render.VertexFormat system) {
			this.format = system;
		}

		public <A extends ShaderSetting<F>> VBuilder<A> add(ShaderSetting.Factory<A> type) {
			this.factories.add(type);
			return (VBuilder<A>) this;
		}

		public VBuilder<F> ext(RenderPhase extension) {
			if(this.extensions == null) {
				this.extensions = new ArrayList<>();
			}
			this.extensions.add(extension);
			return this;
		}

		public RenderLayer build(Supplier<net.minecraft.client.render.Shader> shader) {
			RenderPhase extension;
			if(this.extensions == null) {
				extension = RenderPhase.EMPTY;
			} else {
				extension = RenderPhase.COMBINE.combine(this.extensions.toArray(RenderPhase[]::new));
			}
			return new RenderLayerImpl<>(this.factories, this.format, shader, extension);
		}
	}

	private class PrimitiveSupplierImpl implements PrimitiveSupplier {
		private final VertexRendererImpl impl;
		private final VertexSetting<?> next;
		private Global outest;

		public PrimitiveSupplierImpl(VertexRendererImpl impl, VertexSetting<?> next) {
			this.impl = impl;
			this.next = next;
		}

		@Override
		public PrimitiveImpl<?> create() {
			BuiltDataStack stack = RenderLayerImpl.this.stack.build();
			PrimitiveImpl primitive = new PrimitiveImpl<>(this.impl, RenderLayerImpl.this, this.next, stack);
			primitive.outest = this.outest;
			return primitive;
		}

		@Override
		public DataStack getActive() {
			return RenderLayerImpl.this.stack;
		}
	}


}
