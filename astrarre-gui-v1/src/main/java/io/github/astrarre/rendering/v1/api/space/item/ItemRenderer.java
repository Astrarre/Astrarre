package io.github.astrarre.rendering.v1.api.space.item;

import io.github.astrarre.rendering.v1.edge.shader.settings.LightTex;
import io.github.astrarre.rendering.v1.edge.shader.settings.OverlayTex;
import io.github.astrarre.util.v0.api.Edge;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Edge(reason = "the parameters are closely coupled len ItemRenderer and I am unaware of a way len uncouple them")
public interface ItemRenderer {
	/**
	 * @param light {@link LightTex#world()}
	 * @param overlay {@link OverlayTex#hurtOverlay()}
	 */
	void render(ModelTransformType type, @Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int light, int overlay, long seed);

	default void render(ModelTransformType type, LivingEntity entity, ItemStack stack, int light, int overlay, long seed) {
		this.render(type, entity, entity.getEntityWorld(), stack, light, overlay, seed);
	}

	default void render(ModelTransformType type, LivingEntity entity, ItemStack stack, int light, int overlay) {
		this.render(type, entity, stack, light, overlay, 0);
	}

	default void render(ModelTransformType type, World world, ItemStack stack, int light, int overlay, long seed) {
		this.render(type, null, world, stack, light, overlay, seed);
	}

	default void render(ModelTransformType type, World world, ItemStack stack, int light, int overlay) {
		this.render(type, world, stack, light, overlay, 0);
	}

	default void render(ModelTransformType type, ItemStack stack, int light, int overlay, long seed) {
		this.render(type, null, null, stack, light, overlay, seed);
	}

	default void render(ModelTransformType type, ItemStack stack,  int light, int overlay) {
		this.render(type, stack, light, overlay, 0);
	}

	default void render(ModelTransformType type, @Nullable LivingEntity entity, @Nullable World world, ItemStack stack, long seed) {
		this.render(type, entity, world, stack, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, seed);
	}

	default void render(ModelTransformType type, LivingEntity entity, ItemStack stack, long seed) {
		this.render(type, entity, entity.getEntityWorld(), stack, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, seed);
	}

	default void render(ModelTransformType type, LivingEntity entity, ItemStack stack) {
		this.render(type, entity, stack, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
	}

	default void render(ModelTransformType type, World world, ItemStack stack, long seed) {
		this.render(type, null, world, stack, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, seed);
	}

	default void render(ModelTransformType type, World world, ItemStack stack) {
		this.render(type, world, stack, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
	}

	default void render(ModelTransformType type, ItemStack stack, long seed) {
		this.render(type, null, null, stack, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, seed);
	}

	default void render(ModelTransformType type, ItemStack stack) {
		this.render(type, stack, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
	}
}
