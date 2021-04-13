package io.github.astrarre.gui.v0.api.base.widgets;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

public class AImage extends ADrawable {
	public static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "image"), AImage::new);

	public final Sprite.Sized image;
	public AImage(Sprite.Sized image) {
		this(ENTRY, image);
	}

	protected AImage(DrawableRegistry.@Nullable Entry id, Sprite.Sized image) {
		super(id);
		this.image = image;
		this.setBounds(Polygon.rectangle(image.width, image.height));
	}

	protected AImage(DrawableRegistry.Entry entry, NBTagView view) {
		super(entry);
		this.image = Sprite.SIZED_SER.read(view, "image");
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		graphics.drawSprite(this.image);
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		output.putSerializable("image", this.image);
	}
}
