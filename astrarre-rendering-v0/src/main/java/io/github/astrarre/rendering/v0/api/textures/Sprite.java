package io.github.astrarre.rendering.v0.api.textures;

import io.github.astrarre.itemview.v0.api.Serializable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.internal.textures.Cutout;
import io.github.astrarre.rendering.internal.textures.SpritePath;
import io.github.astrarre.rendering.internal.textures.TexturePath;
import io.github.astrarre.rendering.v0.api.textures.client.ManagedSprite;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.client.MinecraftClient;

/**
 * A sprite is an image or animated image do <b>NOT</b> implement this interface
 */
public interface Sprite extends Serializable {
	Serializer<Sprite> SERIALIZER = Serializer.of(tag -> {
		NBTagView view = tag.asTag();
		switch (view.getInt("id")) {
		case SpritePath.ID:
			return SpritePath.SERIALIZER.read(tag);
		case SpritePath.PART_ID:
			return SpritePath.PART.read(tag);
		case TexturePath.ID:
			return TexturePath.SERIALIZER.read(tag);
		case Cutout.ID:
			return Cutout.SERIALIZER.read(tag);
		default:
			throw new UnsupportedOperationException("unknown sprite type");
		}
	});

	Serializer<Sized> SIZED_SER = Serializer.of((view) -> {
		NBTagView tag = view.asTag();
		return new Sized(SERIALIZER.read(tag, "sprite"), tag.getFloat("width"), tag.getFloat("height"));
	});

	/**
	 * @return an atlas managed sprite. When called on the server, it creates a serverside representation
	 */
	static Sprite of(Id atlasId, Id textureId) {
		if (Validate.IS_CLIENT) {
			MinecraftClient client = MinecraftClient.getInstance();
			return ManagedSprite.of(client.getSpriteAtlas(atlasId.to()).apply(textureId.to()));
		} else {
			return new SpritePath(atlasId, textureId);
		}
	}

	static Sprite of(Id textureId) {
		return new TexturePath(textureId);
	}

	/**
	 * These should be normalized coordinates, so [0-1] where [0, 0] is the top left corner, and [1, 1] is the bottom right corner. The offsets and
	 * dimensions are relative to the width/height of the current sprite.
	 */
	default Sprite cutout(float offsetX, float offsetY, float width, float height) {
		float fox = this.offsetX() + offsetX * this.width(), foy = this.offsetY() + offsetY * this.height();
		float fw = this.width() * width, fh = this.height() * height;
		return new Cutout(this.textureId(), fox, foy, fw, fh);
	}

	default Sprite.Sized cutoutSized(float offsetX, float offsetY, float width, float height, float totalWidth, float totalHeight) {
		return this.cutout(offsetX / totalWidth, offsetY / totalHeight, width / totalWidth, height / totalHeight).sized(width, height);
	}

	/**
	 * the offsetX/offsetY is not necessarily in pixels
	 */
	float offsetX();

	/**
	 * the width/height is not necessarily in pixels
	 */
	float width();

	float offsetY();

	float height();

	Id textureId();

	default Sized sized(float width, float height) {
		return new Sized(this, width, height);
	}

	class Sized implements Serializable {
		public final Sprite sprite;
		public final float width, height;

		public Sized(Sprite sprite, float width, float height) {
			this.sprite = sprite;
			this.width = width;
			this.height = height;
		}

		public Sized ofSize(float width, float height) {
			return this.sprite.sized(width, height);
		}

		public Sized scale(float scaleX, float scaleY) {
			return new Sized(this.sprite, this.width * scaleX, this.height * scaleY);
		}

		/**
		 * @param offsetX all normalized coordinates [0-1]
		 * @param offsetY [0-1]
		 * @param width [0-1]
		 * @param height [0-1]
		 */
		public Sized cutout(float offsetX, float offsetY, float width, float height) {
			return this.sprite.cutout(offsetX, offsetY, width, height).sized(width * this.width, height * this.height);
		}

		public Sized cutToSize(float width, float height) {
			return this.cutout(0, 0, width / this.width, height / this.height);
		}

		@Override
		public NBTagView save() {
			NBTagView.Builder view = NBTagView.builder().putFloat("width", this.width).putFloat("height", this.height);
			view.put("sprite", Sprite.SERIALIZER, this.sprite);
			return view;
		}
	}


}
