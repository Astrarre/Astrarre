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
	Global outest;
	DataStack active = OpenGLRendererImpl.CURRENT;
	BuiltDataStack built;

	public PrimitiveImpl(OpenGLRendererImpl $, VertexFormat<?> format, T next) {
		this.$ = $;
		this.next = next;
		this.format = format;
	}

	@Override
	public T quad() {
		// todo setup shader settings
		this.$.swapTo(outest, this.getBuilt(), this.format, DrawMode.QUADS);
		return this.next;
	}

	@Override
	public T line() {
		this.$.swapTo(outest, this.getBuilt(), this.format, DrawMode.LINES);
		return this.next;
	}

	@Override
	public T continuousLine() {
		this.$.swapTo(outest, this.getBuilt(), this.format, DrawMode.LINE_STRIP);
		return this.next;
	}

	@Override
	public T triangle() {
		this.$.swapTo(outest, this.getBuilt(), this.format, DrawMode.TRIANGLES);
		return this.next;
	}

	@Override
	public void close() {

	}

	public BuiltDataStack getBuilt() {
		BuiltDataStack stack = this.built;
		if(stack == null) {
			this.built = stack = OpenGLRendererImpl.CURRENT.build();
			this.active = null;
		}
		return stack;
	}

	@Override
	public DataStack getActive() {
		return this.active;
	}
}
