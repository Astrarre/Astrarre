package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferAccess;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public final class Pos<Next extends VertexSetting> extends VertexSetting<Next> {
	static final Type<Pos<?>> TYPE = type(Pos::new, VertexFormats.POSITION_ELEMENT);

	public Pos(BufferAccess builder, VertexFormat<?> settings, VertexSetting next) {
		super(builder, settings, next);
	}


	public Next pos(float x, float y, float z) {
		this.builder().vertex(x, y, z);
		return this.next;
	}

	public Next pos(Matrix4f matrix, float x, float y, float z) {
		this.builder().vertex(matrix, x, y, z);
		return this.next;
	}

	public Next pos(MatrixStack matrix, float x, float y, float z) {
		return this.pos(matrix.peek().getModel(), x, y, z);
	}
}
