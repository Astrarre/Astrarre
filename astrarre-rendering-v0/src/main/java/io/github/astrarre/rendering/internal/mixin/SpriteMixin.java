package io.github.astrarre.rendering.internal.mixin;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.internal.textures.SpritePath;
import io.github.astrarre.rendering.v0.api.textures.client.ManagedSprite;
import io.github.astrarre.util.v0.api.Id;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;

@Mixin(Sprite.class)
public abstract class SpriteMixin implements ManagedSprite {
	@Shadow @Final private int[] frameXs;
	@Shadow public abstract int getWidth();
	@Shadow public abstract int getHeight();
	@Shadow public abstract SpriteAtlasTexture getAtlas();
	@Shadow public abstract float getMinU();
	@Shadow public abstract float getMinV();
	@Shadow public abstract float getMaxU();
	@Shadow public abstract float getMaxV();

	@Shadow @Final private int[] frameYs;
	@Shadow @Final private Sprite.Info info;
	private int atlasWidth, atlasHeight;
	@Inject(method = "<init>", at = @At("RETURN"))
	public void onInit(SpriteAtlasTexture spriteAtlasTexture,
			Sprite.Info info,
			int maxLevel,
			int atlasWidth,
			int atlasHeight,
			int x,
			int y,
			NativeImage nativeImage,
			CallbackInfo ci) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
	}


	@Override
	public io.github.astrarre.rendering.v0.api.textures.Sprite getTexture(int frame) {
		// todo
		return null;
	}

	@Override
	public void save(NBTagView.Builder tag, String key) {
		NBTagView.Builder builder = NBTagView.builder();
		Serializer.ID.save(builder, "atlasId", Id.of(this.getAtlas().getId()));
		Serializer.ID.save(builder, "textureId", Id.of(this.info.getId()));
		builder.putInt("id", SpritePath.ID);
		tag.putTag(key, builder);
	}

	@Override
	public int frames() {
		return this.frameXs.length;
	}

	@Override
	public int width(int frame) {
		return this.getWidth();
	}

	@Override
	public int height(int frame) {
		return this.getHeight();
	}

	@Override
	public Id textureId() {
		return Id.of(this.getAtlas().getId());
	}

	@Override
	public float offsetX() {
		return this.getMinU();
	}

	@Override
	public float offsetY() {
		return this.getMinV();
	}

	@Override
	public float width() {
		return this.getMaxU() - this.getMinU();
	}

	@Override
	public float height() {
		return this.getMaxV() - this.getMinV();
	}
}
