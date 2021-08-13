package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferAccess;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;

public final class Normal<Next extends VertexSetting<?>> extends VertexSetting<Next> {
	static final Type<Normal<?>> TYPE = type(Normal::new, VertexFormats.NORMAL_ELEMENT);

	public Normal(BufferAccess builder, VertexFormat<?> settings, VertexSetting next) {
		super(builder, settings, next);
	}


	public Next normal(float x, float y, float z) {
		this.builder().normal(x, y, z);
		return this.next;
	}

	public Next normal(Matrix3f matrix, float x, float y, float z) {
		this.builder().normal(matrix, x, y, z);
		return this.next;
	}

	public Next normal(MatrixStack matrix, float x, float y, float z) {
		return this.normal(matrix.peek().getNormal(), x, y, z);
	}
}
