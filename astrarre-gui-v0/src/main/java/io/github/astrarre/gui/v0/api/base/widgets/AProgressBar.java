package io.github.astrarre.gui.v0.api.base.widgets;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

public class AProgressBar extends ADrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "progress_bar"), AProgressBar::new);
	public final SyncedProperty<Float> progress = this.createClientSyncedProperty(Serializer.FLOAT, 0f);
	public final SyncedProperty<Sprite.Sized> textures = this.createClientSyncedProperty(Sprite.SIZED_SER, null), background = this.createClientSyncedProperty(Sprite.SIZED_SER, null);

	public AProgressBar(Sprite.Sized bar, Sprite.Sized background) {
		this(ENTRY, bar, background);
	}

	protected AProgressBar(DrawableRegistry.@Nullable Entry id, Sprite.Sized bar, Sprite.Sized background) {
		super(id);
		this.textures.set(bar);
		this.textures.set(background);
	}

	protected AProgressBar(DrawableRegistry.Entry id, NBTagView tagView) {
		super(id);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		graphics.drawSprite(this.background.get());
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {

	}
}
