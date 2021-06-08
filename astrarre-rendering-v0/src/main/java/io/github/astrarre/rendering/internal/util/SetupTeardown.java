package io.github.astrarre.rendering.internal.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;

public enum SetupTeardown {
	FILL(null) {
		@Override
		public void setup() {
			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
		}

		@Override
		public void teardown() {
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	},
	SPRITE(null) {
		@Override
		public void setup() {
			RenderSystem.enableTexture();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
		}

		@Override
		public void teardown() {
		}
	},
	ITEM(null) {
		@Override
		public void setup() {

		}

		@Override
		public void teardown() {
			RenderSystem.enableDepthTest();
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
