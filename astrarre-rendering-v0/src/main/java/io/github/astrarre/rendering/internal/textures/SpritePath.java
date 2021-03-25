package io.github.astrarre.rendering.internal.textures;

import io.github.astrarre.itemview.v0.api.Serializable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.util.v0.api.Id;

public class SpritePath implements Sprite, Serializable {
	public static final int ID = 1;
	public static final int PART_ID = 2;
	public static final Serializer<Sprite> SERIALIZER = Serializer.of((view) -> {
		NBTagView tag = view.asTag();
		return Sprite.of(Serializer.ID.read(tag, "atlasId"), Serializer.ID.read(tag, "textureId"));
	});
	public static final Serializer<Sprite> PART = Serializer.of((tag) -> {
		NBTagView view = tag.asTag();
		return SERIALIZER.read(view).cutout(view.getFloat("offX"), view.getFloat("offY"), view.getFloat("width"), view.getFloat("height"));
	});
	public final Id atlasId, textureId;

	public SpritePath(Id atlasId, Id textureId) {
		this.atlasId = atlasId;
		this.textureId = textureId;
	}

	@Override
	public Id textureId() {
		return this.atlasId;
	}

	@Override
	public float offsetX() {
		return 0;
	}

	@Override
	public float offsetY() {
		return 0;
	}

	@Override
	public float width() {
		return 1f;
	}

	@Override
	public float height() {
		return 1f;
	}

	@Override
	public Sprite cutout(float offsetX, float offsetY, float width, float height) {
		return new Part(offsetX, offsetY, width, height);
	}

	@Override
	public NbtValue save() {
		NBTagView.Builder builder = NBTagView.builder();
		Serializer.ID.save(builder, "atlasId", this.atlasId);
		Serializer.ID.save(builder, "textureId", this.textureId);
		builder.putInt("id", ID);
		return builder;
	}

	public class Part extends Cutout {
		public Part(float offX, float offY, float width, float height) {
			super(SpritePath.this.textureId(), offX, offY, width, height);
		}

		@Override
		public Sprite cutout(float offsetX, float offsetY, float width, float height) {
			float fox = this.offsetX() + offsetX * this.width(), foy = this.offsetY() + offsetY * this.height();
			float fw = this.width() * width, fh = this.height() * height;
			return new Part(fox, foy, fw, fh);
		}

		@Override
		public NbtValue save() {
			NBTagView.Builder builder = NBTagView.builder()
					                            .putFloat("offX", this.offX)
					                            .putFloat("offY", this.offY)
					                            .putFloat("width", this.width)
					                            .putFloat("height", this.height)
					.putInt("id", PART_ID);
			Serializer.ID.save(builder, "atlasId", SpritePath.this.atlasId);
			Serializer.ID.save(builder, "textureId", SpritePath.this.textureId);
			return builder;
		}
	}
}
