package io.github.astrarre.rendering.internal.ogl;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.v1.edge.VertexRenderer;
import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.setting.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.shader.setting.ShaderSettingInternal;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;
import io.github.astrarre.rendering.v1.edge.vertex.settings.End;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSettingInternal;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;

@SuppressWarnings("unchecked")
public class VertexRendererImpl implements VertexRenderer, BufferSupplier, Function<VertexFormat<?>, Global> {
	final Map<VertexFormat<?>, Global> config;
	final BufferBuilder buffer;
	public MatrixStack stack;
	VertexSetting<?> start = End.INSTANCE, current = End.INSTANCE;
	VertexFormat<?> activeFormat;
	Global oldOutest;
	BuiltDataStack oldStack;
	DrawMode activeMode;

	public VertexRendererImpl(BufferBuilder buffer) {
		this.buffer = buffer;
		this.config = new WeakHashMap<>();
	}

	@Override
	public <F extends Global> F render(VertexFormat<F> format) {
		return (F) this.config.computeIfAbsent(format, this);
	}

	@Override
	public void flush() {
		this.swapTo(null, null, null, null);
	}

	@Override
	public Global apply(VertexFormat<?> format) {
		return format.create(this);
	}

	// this validates incomplete primitives
	public void swapTo(Global outest, BuiltDataStack stack, VertexFormat<?> format, DrawMode mode) {
		// if vertex format is different, we need to setup shaders again
		VertexFormat<?> active = this.activeFormat;
		boolean shaderInit = format != active || stack != this.oldStack;
		if(shaderInit || mode != this.activeMode) {
			BufferBuilder builder = this.buffer;
			if(builder.isBuilding()) {
				builder.next();
				builder.end();
				BufferRenderer.draw(builder);
			}
			this.activeMode = mode;
			this.activeFormat = format;
			if(format != null) {
				builder.begin(mode, format.asMinecraft());
			}
		}

		if(shaderInit) {
			Global old = this.oldOutest;
			BuiltDataStack oldStack = this.oldStack;
			while(old instanceof ShaderSetting<?> setting) {
				stack.reset();
				setting.disable(oldStack);
				old = ShaderSettingInternal.next(setting);
			}
			if(active != null) {
				active.takedownShader();
			}

			if(format != null) {
				format.loadShader();
			}
			while(outest instanceof ShaderSetting<?> setting) {
				stack.reset();
				setting.enable(stack);
				outest = ShaderSettingInternal.next(setting);
			}
			this.oldOutest = outest;
			this.oldStack = stack;
		}
	}

	// this validates incomplete vertexes
	@Override
	public BufferBuilder getBuffer(VertexSetting<?> setting, VertexFormat<?> vertexFormat) {
		VertexSetting<?> next = VertexSettingInternal.next(setting), current = this.current;
		if(setting == this.start) {
			this.buffer.next();
		} else if(!(current == next || next == End.INSTANCE)) {
			if(current == End.INSTANCE) { // starting new primitive of different type
				if(vertexFormat != this.activeFormat) {
					throw new IllegalStateException(
							"activeFormat is not the same as vertexFormat! This may be because you are storing the return of a " + Primitive.class + " method");
				}
				this.start = setting;
			} else { // invalid
				throw new IllegalStateException(
						"vertex settings inserted out of order! You must call all the VertexSettings settings before calling another method in the " + "Renderer or " + Primitive.class);
			}
		}

		this.current = next;
		return this.buffer;
	}
}
