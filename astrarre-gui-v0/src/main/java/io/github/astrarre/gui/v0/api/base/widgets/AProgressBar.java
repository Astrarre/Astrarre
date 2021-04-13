package io.github.astrarre.gui.v0.api.base.widgets;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

public class AProgressBar extends ADrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry
			                                                    .registerForward(Id.create("astrarre-gui-v0", "progress_bar"), AProgressBar::new);
	/**
	 * the progress of the bar, 0 = 0%, 1 = 100%
	 */
	public final SyncedProperty<Float> progress = this.createClientSyncedProperty(NBTType.FLOAT, 0f);
	public final SyncedProperty<Sprite.Sized> full = this.createClientSyncedProperty(
			Sprite.SIZED_SER,
			null), background = this.createClientSyncedProperty(Sprite.SIZED_SER, null);
	public final Direction direction;

	/**
	 * @see AstrarreIcons#FURNACE_PROGRESS_BAR_FULL
	 * @param direction the direction the progress bar should go in
	 */
	public AProgressBar(Sprite.Sized bar, Sprite.Sized background, Direction direction) {
		this(ENTRY, bar, background, direction);
	}

	protected AProgressBar(DrawableRegistry.@Nullable Entry id, Sprite.Sized bar, Sprite.Sized background, Direction direction) {
		super(id);
		this.full.set(bar);
		this.background.set(background);
		this.direction = direction;
		this.setBounds(Polygon.rectangle(bar.width, bar.height));
	}

	protected AProgressBar(DrawableRegistry.Entry id, NBTagView v) {
		super(id);
		this.direction = Direction.SERIALIZER.read(v, "dir");
	}

	public static void init() {
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		this.drawBackground(graphics);
		float progress = this.progress.get();
		if (progress > .01) {
			this.drawProgress(graphics, progress);
		}
	}

	protected void drawBackground(GuiGraphics g3d) {
		g3d.drawSprite(this.background.get());
	}

	protected void drawProgress(GuiGraphics g3d, float progress) {
		switch (this.direction) {
		case RIGHT:
			g3d.drawSprite(this.full.get().cutout(0, 0, progress, 1));
			break;
		case UP:
			try (Close ignored = g3d.applyTransformation(Transformation.translate(0, (1 - progress) * this.full.get().height, 0))) {
				g3d.drawSprite(this.full.get().cutout(0, 1 - progress, 1, progress));
			}
			break;
		case DOWN:
			g3d.drawSprite(this.full.get().cutout(0, 0, 1, progress));
			break;
		case LEFT:
			try (Close ignored = g3d.applyTransformation(Transformation.translate((1 - progress) * this.full.get().height, 0, 0))) {
				g3d.drawSprite(this.full.get().cutout(1 - progress, 0, progress, 1));
			}
			break;
		}
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		output.put("dir", Direction.SERIALIZER, this.direction);
	}

	public enum Direction {
		UP, DOWN, RIGHT, LEFT;
		public static final Serializer<Direction> SERIALIZER = Serializer.ofEnum(Direction.class);
	}
}


