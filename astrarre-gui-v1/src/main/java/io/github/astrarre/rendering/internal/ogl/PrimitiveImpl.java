package io.github.astrarre.rendering.internal.ogl;

import io.github.astrarre.rendering.v1.edge.Primitive;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.mem.DataStack;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;
import io.github.astrarre.rendering.v1.edge.vertex.settings.VertexSetting;

import net.minecraft.client.render.VertexFormat.DrawMode;

public final class PrimitiveImpl<T extends VertexSetting<?>> implements Primitive<T> {
	final OpenGLRendererImpl $;
	final T next;
	final VertexFormat<?> format;
	final BuiltDataStack stack;
	Global outest;

	public PrimitiveImpl(OpenGLRendererImpl $, VertexFormat<?> format, T next, BuiltDataStack stack) {
		this.$ = $;
		this.next = next;
		this.format = format;
		this.stack = stack;
	}

	@Override
	public T quad() {
		// todo setup shader settings
		this.$.swapTo(this.outest, this.stack, this.format, DrawMode.QUADS);
		return this.next;
	}

	@Override
	public T line() {
		this.$.swapTo(this.outest, this.stack, this.format, DrawMode.LINES);
		return this.next;
	}

	@Override
	public T continuousLine() {
		this.$.swapTo(this.outest, this.stack, this.format, DrawMode.LINE_STRIP);
		return this.next;
	}

	@Override
	public T triangle() {
		this.$.swapTo(this.outest, this.stack, this.format, DrawMode.TRIANGLES);
		return this.next;
	}

	@Override
	public void close() {
	}

	@Override
	public DataStack getActive() {
		throw new UnsupportedOperationException();
	}
}
