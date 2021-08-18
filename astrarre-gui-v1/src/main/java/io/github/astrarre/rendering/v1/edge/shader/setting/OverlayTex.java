package io.github.astrarre.rendering.v1.edge.shader.setting;

import io.github.astrarre.rendering.v1.edge.shader.Global;

public class OverlayTex<T extends Global> extends Img<T> {
	static final Factory<OverlayTex<?>> FACTORY = val -> new OverlayTex<>(val, 1);

	public OverlayTex(T val, int index) {
		super(val, index);
	}

	/**
	 * Bind the overlay texture used to turn entities red when hit. It looks like this:
	 * <br>
	 * <img src="{@docRoot}/doc-files/hurt_overlay.png">
	 */
	public void hurtOverlay() {

	}
}
