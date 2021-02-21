package io.github.astrarre.rendering.internal.util;

import com.mojang.blaze3d.systems.RenderSystem;

public enum SetupTeardown {
	FILL(null) {
		@Override
		public void setup() {
			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.defaultBlendFunc();
		}

		@Override
		public void teardown() {
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	},
	GRADIENT(FILL) {
		@Override
		public void setup() {
			RenderSystem.shadeModel(7425);
		}

		@Override
		public void teardown() {
			RenderSystem.shadeModel(7424);
		}
	};

	public final SetupTeardown extendsFrom;
	SetupTeardown(SetupTeardown dependency) {
		this.extendsFrom = dependency;
	}

	/**
	 * any setup unique to this function
	 */
	public abstract void setup();

	/**
	 * any teardown unique to this function
	 */
	public abstract void teardown();
}
