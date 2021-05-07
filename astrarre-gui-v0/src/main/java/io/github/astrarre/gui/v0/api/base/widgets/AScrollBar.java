package io.github.astrarre.gui.v0.api.base.widgets;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.base.AAggregateDrawable;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Val;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Mouse;
import net.minecraft.util.math.MathHelper;

/**
 * the aggregate is just to shift the button instance
 */
public class AScrollBar extends AAggregateDrawable implements Interactable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0:scrollbar"), AScrollBar::new);
	/**
	 * listeners do not get synced to the client or server!
	 */
	public final Val<Float> percentage;

	protected final float height;
	protected float scrollHeight;
	/**
	 * @param scrollbar the scrollbar drawable, the transformation for this object is overriden, and it must implement bounds
	 */
	public AScrollBar(ADrawable scrollbar, Val<Float> percentage, float height) {
		this(ENTRY, scrollbar, percentage, height);
	}

	protected AScrollBar(DrawableRegistry.@Nullable Entry id, ADrawable scrollbar, Val<Float> percentage, float height) {
		super(id);
		this.add(scrollbar);
		this.percentage = percentage;
		this.height = height;
	}

	protected AScrollBar(DrawableRegistry.Entry entry, NBTagView view) {
		super(entry, view);
		this.percentage = Val.ofFloat(view.getFloat("percentage"));
		this.height = view.getFloat("height");
	}

	public static void init() {}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		this.scrollHeight = this.getScrollHeight();
	}

	@Override
	protected boolean onSync(ADrawable drawable) {
		this.percentage.addListener((old, current) -> {
			this.scrollHeight = this.getScrollHeight();
			drawable.setTransformation(Transformation.translate(0, current * this.scrollHeight, 0));
		});
		return super.onSync(drawable);
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		for (ADrawable drawable : this.drawables) {
			drawable.render(container, graphics, tickDelta);
		}
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		super.write0(container, output);
		output.putFloat("percentage", this.percentage.get());
		output.putFloat("height", this.height);
	}

	@Override
	public boolean mouseDragged(RootContainer container, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		float deltaPercent = (float) (deltaY / this.scrollHeight); // rough approximation
		this.percentage.set(Math.min(Math.max(this.percentage.get() + deltaPercent, 0), 1));
		return true;
	}

	protected float getScrollHeight() {
		float buttonHeight = this.drawables.get(0).getBounds().getEnclosing().getY(2);
		return this.height - buttonHeight; // the max y coordinate we can go to
	}
}
