package io.github.astrarre.rendering.internal.textures;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.util.v0.api.Id;

public class Cutout implements Sprite {
	public static final Serializer<Cutout> SERIALIZER = Serializer.of((v) -> {
		NBTagView tag = v.asTag();
		return new Cutout(Serializer.ID.read(tag, "textureId"),
				tag.getFloat("offX"),
				tag.getFloat("offY"),
				tag.getFloat("width"),
				tag.getFloat("height"));
	});
	public static final int ID = 0;

	public final Id sprite;
	public final float offX, offY, width, height;

	public Cutout(Id sprite, float offX, float offY, float width, float height) {
		this.sprite = sprite;
		this.offX = offX;
		this.offY = offY;
		this.width = width;
		this.height = height;
	}

	@Override
	public float offsetX() {
		return this.offX;
	}

	@Override
	public float width() {
		return this.width;
	}

	@Override
	public float offsetY() {
		return this.offY;
	}

	@Override
	public float height() {
		return this.height;
	}

	@Override
	public Id textureId() {
		return this.sprite;
	}

	@Override
	public NbtValue save() {
		NBTagView.Builder builder = NBTagView.builder().putFloat("offX", this.offX).putFloat("offY", this.offY).putFloat("width", this.width)
				                            .putFloat("height", this.height);

		Serializer.ID.save(builder, "textureId", this.sprite);
		return builder;
	}
}
