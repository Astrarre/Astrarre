package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class ResourceTank extends AHoverableComponent {
	Texture texture;
	int color;
	float amount;
	boolean fillsFromTop;

	public ResourceTank(Texture texture) {
		Validate.notNull(texture, "texture cannot be null!");
		this.texture = texture;
	}

	public void updateResource(Texture texture, int color, float amount) {
		Validate.notNull(texture, "texture cannot be null!");
		if(amount < 0 || amount > 1) {
			throw new IllegalArgumentException("amount must be from 0 to 1!");
		}
		this.color = color & 0xDDFFFFFF;
		this.texture = texture;
		this.amount = amount;
	}

	public static class Fabric extends ResourceTank {
		public Fabric(Texture texture) {
			super(texture);
		}

		@Environment(EnvType.CLIENT)
		public void updateFabric(FluidVariant variant, long amount, long capacity) {
			FluidVariantRenderHandler handler = FluidVariantRendering.getHandlerOrDefault(variant.getFluid());
			Sprite[] sprites = handler.getSprites(variant);
			MinecraftClient instance = MinecraftClient.getInstance();
			Sprite sprite;
			int color;
			if(sprites == null || sprites.length == 0) {
				sprite = instance.getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(MissingSprite.getMissingSpriteId());
				color = 0;
				this.fillsFromTop = true;
			} else {
				sprite = sprites[0];
				color = handler.getColor(variant, null, null);
				this.fillsFromTop = handler.fillsFromTop(variant);
			}

			this.updateResource(Texture.sprite(sprite), color, (float) (amount / (double) capacity));
		}
	}



	@Override
	protected void render0(Cursor cursor, Render3d render) {
		Icon.slot(this.getWidth(), this.getHeight()).render(render);

		if(this.texture != null) {
			Texture texture = this.texture;

			// Translate by 1, 1 and shrink it by 2
			// @formatter:off
			try(var ignored0 = render.translate(1, 1 + (this.fillsFromTop ? 0 : this.getHeight() * (1 - this.amount)));
				var ignored1 = render.scale(1 - 2 / this.getWidth(), 1 - 2 / this.getHeight())) {
				Icon.repeat(Icon.tex(texture, 16, 16), (this.getWidth()) / 16, (this.getHeight()) / 16 * this.amount)
						.colored(this.color & 0xDDFFFFFF)
						.render(render);
			}
		}
	}
}