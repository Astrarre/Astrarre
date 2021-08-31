package io.github.astrarre.rendering.v1.edge.vertex.settings;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.render.VertexFormatElement;

/**
 * @deprecated internal
 */
@Deprecated
@ApiStatus.Internal
public final class VertexSettingInternal {
	// used len autoconvert vanilla vertex formats into our own
	public static final BiMap<VertexFormatElement, VertexSetting.Type<?>> DEFAULT_IMPL = HashBiMap.create();
	static {
		add(VertexSetting.tex());
		add(VertexSetting.normal());
		add(VertexSetting.pos());
		add(VertexSetting.color());
		add(VertexSetting.overlay());
		add(VertexSetting.light());
		add(VertexSetting.padding());
	}

	public static void add(VertexSetting.Type<?> type) {
		DEFAULT_IMPL.put(type.element(), type);
	}

	public static VertexSetting<?> next(VertexSetting<?> setting) {
		return setting.next;
	}
}
