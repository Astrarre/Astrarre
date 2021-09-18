package io.github.astrarre.rendering.v1.edge.data;

import java.nio.ByteBuffer;

public interface ShaderSetting<Next extends InitStage> extends InitStage {

	void setBuffer(ByteBuffer object);

	@Override
	AccessHack __();
}
