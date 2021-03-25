package io.github.astrarre.rendering.internal.textures;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;

public class TexturePath implements Sprite {
	public static final Serializer<TexturePath> SERIALIZER = Serializer.of((tag) -> new TexturePath(Serializer.ID.read(tag.asTag(), "texture")));
	public static final int ID = 3;
	public static final TextureManager MANAGER = MinecraftClient.getInstance().getTextureManager();
	public final Id texture;

	public TexturePath(Id texture) {
		this.texture = texture;
	}

	@Override
	public Id textureId() {
		return this.texture;
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
	public NbtValue save() {
		NBTagView.Builder builder = NBTagView.builder();
		Serializer.ID.save(builder, "texture", this.texture);
		return builder;
	}
}
