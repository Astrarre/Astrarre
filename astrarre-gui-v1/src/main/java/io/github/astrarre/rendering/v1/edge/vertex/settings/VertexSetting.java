package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferAccess;
import io.github.astrarre.rendering.v1.edge.shader.ShaderSetting;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormatElement;

@SuppressWarnings("unchecked")
public abstract class VertexSetting<Next extends VertexSetting> {
	public static <A extends VertexSetting<?>> Type<A> type(Factory<A> factory, VertexFormatElement element) {
		return new Type<>(factory, element);
	}

	final BufferAccess builder;
	final VertexFormat<?> settings;
	final Next next;

	public VertexSetting(BufferAccess builder, VertexFormat<?> settings, VertexSetting next) {
		this.builder = builder;
		this.settings = settings;
		this.next = (Next) next;
	}

	public static <C extends VertexSetting<?>, T extends Pos<C>> Type<T> pos() {
		return (Type<T>) Pos.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Tex<C>> Type<T> tex() {
		return (Type<T>) Tex.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Color<C>> Type<T> color() {
		return (Type<T>) Color.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Normal<C>> Type<T> normal() {
		return (Type<T>) Normal.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Overlay<C>> Type<T> overlay() {
		return (Type<T>) Overlay.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Light<C>> Type<T> light() {
		return (Type<T>) Light.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Pad<C>> Type<T> padding() {
		return (Type<T>) Pad.TYPE;
	}

	protected BufferBuilder builder() {
		return this.builder.getBuffer(this, this.settings);
	}

	public record Type<A extends VertexSetting<?>>(Factory<A> factory, VertexFormatElement element) {}

	public interface Factory<A extends VertexSetting<?>> {
		@SuppressWarnings("unchecked")
		default <T extends VertexSetting<?>> VertexSetting<T> create(BufferAccess builder, VertexFormat<?> settings, T val) {
			return (VertexSetting<T>) this.create0(builder, settings, val);
		}

		A create0(BufferAccess builder, VertexFormat<?> settings, VertexSetting next);
	}
}
