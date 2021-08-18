package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormatElement;

@SuppressWarnings({
		"unchecked",
		"rawtypes"
})
public abstract class VertexSetting<Next extends VertexSetting> {
	public static <A extends VertexSetting<?>> Type<A> type(Factory<A> factory, VertexFormatElement element) {
		return new Type<>(factory, element);
	}

	final BufferSupplier builder;
	final VertexFormat<?> settings;
	final Next next;

	public VertexSetting(BufferSupplier builder, VertexFormat<?> settings, VertexSetting<?> next) {
		this.builder = builder;
		this.settings = settings;
		this.next = (Next) next;
	}

	public static <C extends VertexSetting<?>, T extends Pos<C>> Type<T> pos() {
		return (Type) Pos.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Tex<C>> Type<T> tex() {
		return (Type) Tex.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Color<C>> Type<T> color() {
		return (Type) Color.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Normal<C>> Type<T> normal() {
		return (Type) Normal.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Overlay<C>> Type<T> overlay() {
		return (Type) Overlay.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Light<C>> Type<T> light() {
		return (Type) Light.TYPE;
	}
	public static <C extends VertexSetting<?>, T extends Pad<C>> Type<T> padding() {
		return (Type) Pad.TYPE;
	}

	protected BufferBuilder builder() {
		return this.builder.getBuffer(this, this.settings);
	}

	public record Type<A extends VertexSetting<?>>(Factory<A> factory, VertexFormatElement element) {}

	public interface Factory<A extends VertexSetting<?>> {
		@SuppressWarnings("unchecked")
		default <T extends VertexSetting<?>> VertexSetting<T> create(BufferSupplier builder, VertexFormat<?> settings, T val) {
			return (VertexSetting<T>) this.create0(builder, settings, val);
		}

		A create0(BufferSupplier builder, VertexFormat<?> settings, VertexSetting<?> next);
	}
}
