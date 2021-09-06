package io.github.astrarre.rendering.internal;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;

enum SetupImpl implements Renderer2DImpl.Setup {
	DEFAULT {
		@Override
		public void setup(BufferBuilder builder) {

		}

		@Override
		public void takedown(BufferBuilder builder) {
			builder.end();
			BufferRenderer.draw(builder);
		}
	}, LINE {
		@Override
		public void setup(BufferBuilder builder) { // todo fix
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			builder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
			RenderSystem.lineWidth(3.0F);
		}

		@Override
		public void takedown(BufferBuilder builder) {
			DEFAULT.takedown(builder);
			RenderSystem.enableTexture();
			RenderSystem.depthMask(true);
			RenderSystem.lineWidth(1);
		}
	}, TEXTURE {
		@Override
		public void setup(BufferBuilder builder) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		}

		@Override
		public void takedown(BufferBuilder builder) {
			DEFAULT.takedown(builder);
		}
	}, OUTLINE {
		@Override
		public void setup(BufferBuilder builder) {
			// line loop would be most effecient however minecraft doesn't have it and im too lazy len add it
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			builder.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
			RenderSystem.lineWidth(3.0F);
		}

		@Override
		public void takedown(BufferBuilder builder) {
			LINE.takedown(builder);
		}
	}, POS_COLOR {
		@Override
		public void setup(BufferBuilder builder) {
			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.defaultBlendFunc();
			RenderSystem.colorMask(true, true, true, true);
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
		}

		@Override
		public void takedown(BufferBuilder builder) {
			DEFAULT.takedown(builder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	}, QUAD {
		@Override
		public void setup(BufferBuilder builder) {
			POS_COLOR.setup(builder);
			builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		}

		@Override
		public void takedown(BufferBuilder builder) {
			POS_COLOR.takedown(builder);
		}
	}, TRIANGLE {
		@Override
		public void setup(BufferBuilder builder) {
			POS_COLOR.setup(builder);
			builder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
		}

		@Override
		public void takedown(BufferBuilder builder) {
			POS_COLOR.takedown(builder);
		}
	}, ITEM {
		@Override
		public void setup(BufferBuilder builder) {
			var texture = MinecraftClient.getInstance().getTextureManager();
			texture.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
			RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}

		@Override
		public void takedown(BufferBuilder builder) {
			POS_COLOR.takedown(builder);
		}
	}
}
