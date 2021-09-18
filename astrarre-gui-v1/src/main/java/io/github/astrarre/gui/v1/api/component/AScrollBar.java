package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.gui.v1.api.component.button.AButton;
import io.github.astrarre.gui.v1.api.listener.component.ProgressChangeListener;
import io.github.astrarre.gui.v1.api.listener.cursor.ClickType;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.plane.icon.Icons;
import io.github.astrarre.rendering.v1.api.plane.icon.PixelatedTriangleIcon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.rendering.v1.api.util.Axis2d;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

/**
 * A scrollbar or slider
 */
public class AScrollBar extends AHoverableComponent implements MouseListener, FocusableComponent, ToggleableComponent {

	public final Access<ProgressChangeListener> onChange = new Access<>("astrarre", "on_change", array -> (old, new_) -> {
		for(ProgressChangeListener listener : array) {
			listener.onChange(old, new_);
		}
	});

	public final ARootPanel root;
	public final Icon.Group bar;
	public final Icon background;
	public final Axis2d axis;
	float progress, oldProgress;

	public AScrollBar(ARootPanel root, Icon.Group bar, Icon background, Axis2d axis) {
		this.root = root;
		this.axis = axis;
		bar.requireUniformSize();
		this.bar = bar;
		this.background = background;
		this.setBounds(background.width(), background.height());
		this.lockBounds(true);
	}

	/**
	 * Creates a new component that is this component paired with an up and down button
	 */
	public APanel withButtons() {
		Axis2d axis = this.axis;
		float dims = axis.n(this.background.height(), this.background.width());
		Icon.Group upGroup, downGroup;

		if(MathHelper.approximatelyEquals(dims, Icons.DOWN_ARROW.height())) { // ~7
			upGroup = axis.n(Icons.Groups.LEFT_ARROW, Icons.Groups.UP_ARROW);
			downGroup = axis.n(Icons.Groups.RIGHT_ARROW, Icons.Groups.DOWN_ARROW);
		} else {
			var icon = new PixelatedTriangleIcon(0xff444444, dims - 4, dims - 4);
			var buttons = Icons.Groups.button(dims, dims);
			upGroup = buttons.withOverlay(icon.offset(2, 1.5f));
			var down = icon.transformUnaligned(Transform3d.rotate(Direction.Axis.Z, AngleFormat.DEGREES, 180));
			downGroup = buttons.withOverlay(down.offset(dims - 2, dims - 1.5f));
		}

		APanel panel = new APanel();
		AButton up = AButton.button(upGroup, c -> this.setProgress(this.getProgress() - .1f)),
				down = AButton.button(downGroup, c -> this.setProgress(this.getProgress() + .1f));

		panel.add(up);

		float offX = dims, offY = dims;
		var next = Transform3d.translate(axis.x(offX), axis.y(offY), 0);
		panel.add(this.with(next));

		offX += this.background.width();
		offY += this.background.height();
		next = Transform3d.translate(axis.x(offX), axis.y(offY), 0);

		panel.add(down.with(next));

		return panel;
	}

	@Override
	public boolean mouseClicked(Cursor cursor, ClickType type) {
		if(this.isEnabled()) {
			float pixels = this.pixels();
			this.progress = MathHelper.clamp(this.axis.n(cursor.x(), cursor.y()) / pixels, 0, 1);
			this.globalMouseListener(true);
			this.root.requestFocus(this);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(Cursor cursor, ClickType type) {
		if(this.isEnabled()) {
			this.globalMouseListener(false);
			this.setProgress(this.progress);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(Cursor cursor, ClickType type, float deltaX, float deltaY) {
		if(this.isEnabled()) {
			float pixels = this.pixels();
			this.progress = MathHelper.clamp((this.progress * pixels + (this.axis.n(deltaX, deltaY) / 2)) / pixels, 0, 1);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(Cursor cursor, float scroll) {
		if(this.isEnabled()) {
			float pixels = this.pixels();
			this.progress = MathHelper.clamp((this.progress * pixels + scroll) / pixels, 0, 1);
			return true;
		} else {
			return false;
		}
	}

	public float getProgress() {
		return this.progress;
	}

	public AScrollBar setProgress(float progress) {
		progress = MathHelper.clamp(progress, 0, 1);
		if(this.oldProgress != progress) {
			this.onUpdateProgress(this.oldProgress, progress);
			this.oldProgress = progress;
		}
		this.progress = progress;
		return this;
	}

	/**
	 * @param progress 0-1
	 */
	protected void onUpdateProgress(float oldProgress, float progress) {
		this.onChange.get().onChange(oldProgress, progress);
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		this.background.render(render);
		float offset = this.pixels() * this.progress;
		try(var ignore = render.translate(this.axis.x(offset), this.axis.y(offset), 0)) {
			if(this.isIn(cursor)) {
				if(cursor.isPressed(ClickType.Standard.LEFT)) {
					this.bar.pressed().render(render);
				} else {
					this.bar.hover().render(render);
				}
			} else {
				this.bar.normal().render(render);
			}
		}
	}

	protected float pixels() {
		if(this.axis.isX()) {
			return this.background.width() - this.bar.normal().width();
		} else {
			return this.background.height() - this.bar.normal().height();
		}
	}
}
