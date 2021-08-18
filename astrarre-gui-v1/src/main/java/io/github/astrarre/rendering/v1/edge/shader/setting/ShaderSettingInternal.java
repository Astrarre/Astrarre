package io.github.astrarre.rendering.v1.edge.shader.setting;

import io.github.astrarre.rendering.v1.edge.shader.Global;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated internal
 */
@Deprecated
@ApiStatus.Internal
public final class ShaderSettingInternal {

	public static Global next(ShaderSetting<?> setting) {
		return setting.next;
	}
}
