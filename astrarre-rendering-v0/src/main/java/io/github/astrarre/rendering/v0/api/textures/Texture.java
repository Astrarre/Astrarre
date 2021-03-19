package io.github.astrarre.rendering.v0.api.textures;

import java.util.Objects;

import io.github.astrarre.itemview.v0.api.Serializable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.util.Identifier;

/**
 * a path to an image, and it's size
 */
public class Texture implements Serializable {
	public static final Serializer<Texture> SERIALIZER = Serializer.of(Texture::new);
	private final Identifier identifier;
	private final int width, height;

	public Texture(String modid, String path, int width, int height) {
		this(Id.create(modid, path), width, height);
	}

	/**
	 * @param texture the path to the image
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public Texture(Id texture, int width, int height) {
		this((Identifier) texture, width, height);
	}

	public Texture(Identifier texture, int width, int height) {
		this.identifier = texture;
		this.width = Validate.positive(width, "width");
		this.height = Validate.positive(height, "height");
	}

	public Texture(NBTagView tag, String key) {
		NBTagView view = tag.getTag(key);
		this.identifier = Serializer.ID.read(view, "id").to();
		this.width = view.getInt("width");
		this.height = view.getInt("height");
	}

	public Id getId() {
		return (Id) this.identifier;
	}

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Texture)) {
			return false;
		}

		Texture texture = (Texture) o;

		if (this.width != texture.width) {
			return false;
		}
		if (this.height != texture.height) {
			return false;
		}
		return Objects.equals(this.identifier, texture.identifier);
	}

	@Override
	public int hashCode() {
		int result = this.identifier != null ? this.identifier.hashCode() : 0;
		result = 31 * result + this.width;
		result = 31 * result + this.height;
		return result;
	}

	@Override
	public void save(NBTagView.Builder tag, String key) {
		NBTagView.Builder builder = NBTagView.builder()
				.putInt("height", this.height)
				.putInt("width", this.width);
		Serializer.ID.save(builder, "id", Id.of(this.identifier));
		tag.putTag(key, builder);
	}
}
