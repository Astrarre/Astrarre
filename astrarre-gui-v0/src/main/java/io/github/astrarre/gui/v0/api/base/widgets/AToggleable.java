package io.github.astrarre.gui.v0.api.base.widgets;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

/**
 * a toggleable image (toggles between 2 different images)
 * @see AToggleable#enabled
 */
public final class AToggleable extends ADrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "toggleable"), AToggleable::new);
	public final SyncedProperty<Boolean> enabled = this.createClientSyncedProperty(NBTType.BOOL, false);
	public final Sprite.Sized on, off;

	/**
	 * @param on the 'on' texture
	 * @param off the 'off' texture
	 */
	public AToggleable(Sprite.Sized on, Sprite.Sized off) {
		super(ENTRY);
		this.setBounds(Polygon.rectangle(off.width, off.height));
		this.on = on;
		this.off = off;
	}

	private AToggleable(DrawableRegistry.Entry entry, NBTagView view) {
		super(entry);
		this.on = Sprite.SIZED_SER.read(view, "on");
		this.off = Sprite.SIZED_SER.read(view, "off");
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		if(this.enabled.get()) {
			graphics.drawSprite(this.on);
		} else {
			graphics.drawSprite(this.off);
		}
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		output.putSerializable("on", this.on);
		output.putSerializable("off", this.off);
	}
}
