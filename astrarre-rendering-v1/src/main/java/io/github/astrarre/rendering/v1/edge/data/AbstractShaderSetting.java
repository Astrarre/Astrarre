package io.github.astrarre.rendering.v1.edge.data;

import java.nio.ByteBuffer;

public abstract class AbstractShaderSetting<Next extends InitStage> implements ShaderSetting<Next> {
	ByteBuffer buffer;

	public ByteBuffer getBuffer() {
		if(this.buffer != null) {
			return this.buffer;
		} else {
			throw new UnsupportedOperationException("ByteBuffer is null!");
		}
	}

	@Override
	public void setBuffer(ByteBuffer object) {
		this.buffer = object;
	}

	@Override
	public final AccessHack __() {
		return null;
	}
}
