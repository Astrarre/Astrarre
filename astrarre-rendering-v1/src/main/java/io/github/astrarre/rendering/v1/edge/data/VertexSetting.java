package io.github.astrarre.rendering.v1.edge.data;

import java.nio.ByteBuffer;

public interface VertexSetting<Next extends VertexSetting<?>> {
	void setBuffer(ByteBuffer object);

	AccessHack __();
}
