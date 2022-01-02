package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class ResourceTank extends AHoverableComponent {
	Texture texture;
	int color = 0;
	float amount;
	boolean fillsFromTop;

	public ResourceTank() {
	}

	public void updateResource(Texture texture, int color, float amount, boolean fillsFromTop) {
		if(amount < 0 || amount > 1) {
			throw new IllegalArgumentException("amount must be from 0 to 1!");
		}
		this.color = color & 0xDDFFFFFF;
		this.texture = texture;
		this.amount = amount;
		this.fillsFromTop = fillsFromTop;
	}

	public static class Fabric extends ResourceTank {
		FluidVariantRenderHandler handler;
		FluidVariant variant;
		public Fabric() {
			if(Validate.IS_CLIENT) {
				this.tooltipDirect(builder -> {
					builder.text(this.color | 0xFF000000, false).render(this.amount + "dp");
					if(this.handler != null) {
						List<Text> tooltip = new ArrayList<>();
						boolean tooltips = MinecraftClient.getInstance().options.advancedItemTooltips;
						TooltipContext.Default context = tooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL;
						this.handler.appendTooltip(this.variant, tooltip, context);
						for(Text text : tooltip) {
							builder.text(text, false);
						}
					}
				});
			}
		}

		@Environment(EnvType.CLIENT)
		public void updateFabric(FluidVariant variant, long amount, long capacity) {
			FluidVariantRenderHandler handler = FluidVariantRendering.getHandlerOrDefault(variant.getFluid());
			this.handler = handler;
			this.variant = variant;
			Sprite[] sprites = handler.getSprites(variant);
			Sprite sprite;
			int color;
			boolean fillsFromTop;
			if(sprites == null || sprites.length == 0) {
				MinecraftClient instance = MinecraftClient.getInstance();
				sprite = instance.getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(MissingSprite.getMissingSpriteId());
				color = 0;
				fillsFromTop = true;
			} else {
				sprite = sprites[0];
				color = handler.getColor(variant, null, null);
				fillsFromTop = handler.fillsFromTop(variant);
			}

			this.updateResource(Texture.sprite(sprite), color, (float) (amount / (double) capacity), fillsFromTop);
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
				Icon.repeat(Icon.tex(texture, this.color,16, 16), (this.getWidth()) / 16, (this.getHeight()) / 16 * this.amount)
						.render(render);
			}
		}
	}
}