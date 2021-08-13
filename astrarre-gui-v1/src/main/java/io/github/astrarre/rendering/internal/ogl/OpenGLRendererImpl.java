package io.github.astrarre.rendering.internal.ogl;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

import io.github.astrarre.rendering.internal.BufferAccess;
import io.github.astrarre.rendering.v1.edge.OpenGLRenderer;
import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.mem.DataStack;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSettingInternal;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;
import io.github.astrarre.rendering.v1.edge.vertex.settings.End;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSettingInternal;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat.DrawMode;

@SuppressWarnings("unchecked")
public class OpenGLRendererImpl implements OpenGLRenderer, BufferAccess, Function<VertexFormat<?>, Global> {
	static final Map<VertexFormat<?>, Global> CONFIGS = new WeakHashMap<>();
	static final DataStack CURRENT = new DataStack();
	final BufferBuilder buffer;

	VertexSetting<?> start = End.INSTANCE, current = End.INSTANCE;
	VertexFormat<?> activeFormat;
	Global oldOutest;
	BuiltDataStack oldStack;
	DrawMode activeMode;

	public OpenGLRendererImpl(BufferBuilder buffer) {
		this.buffer = buffer;
	}

	public void flush() {
		this.swapTo(null, null, null, null);
	}

	@Override
	public <F extends Global> F render(VertexFormat<F> format) {
		return (F) CONFIGS.computeIfAbsent(format, this);
	}

	@Override
	public Global apply(VertexFormat<?> format) {
		return format.create(this);
	}

	// this validates incomplete primitives
	public void swapTo(Global outest, BuiltDataStack stack, VertexFormat<?> format, DrawMode activeMode) { // todo remove, redraw can be moved later
		// if vertex format is different, we need to setup shaders again
		boolean shaderInit = format != this.activeFormat;
		if(shaderInit) {
			Global old = this.oldOutest;
			BuiltDataStack oldStack = this.oldStack;
			while(old instanceof ShaderSetting<?> setting) {
				setting.takedown(oldStack);
				old = ShaderSettingInternal.next(setting);
			}

			while(outest instanceof ShaderSetting<?> setting) {
				setting.setup(stack);
				outest = ShaderSettingInternal.next(setting);
			}
			this.oldOutest = outest;
			this.oldStack = stack;
		}

		if(shaderInit || activeMode != this.activeMode) {
			BufferBuilder builder = this.buffer;
			builder.end();
			BufferRenderer.draw(builder);
			if(format != null) {
				builder.begin(activeMode, format.asMinecraft());
			}
		}
	}

	// this validates incomplete vertexes
	@Override
	public BufferBuilder getBuffer(VertexSetting<?> setting, VertexFormat<?> vertexFormat) {
		VertexSetting<?> next = VertexSettingInternal.next(setting), current = this.current;
		if(!(current == next || next == End.INSTANCE || setting == this.start)) {
			if(current == End.INSTANCE) { // starting new primitive of different type
				if(vertexFormat != this.activeFormat) {
					throw new IllegalStateException(
							"activeFormat is not the same as vertexFormat! This may be because you are storing the return of a " + Primitive.class + " method");
				}
				this.start = setting;
			} else { // invalid
				throw new IllegalStateException(
						"vertex settings inserted out of order! You must call all the VertexSettings settings before calling another method in the " +
						"Renderer or " + Primitive.class);
			}
		}

		this.current = next;
		return this.buffer;
	}
}
