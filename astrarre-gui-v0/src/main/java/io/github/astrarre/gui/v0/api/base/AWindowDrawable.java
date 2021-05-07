package io.github.astrarre.gui.v0.api.base;

import java.util.Collection;
import java.util.HashSet;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.rendering.v0.edge.Stencil;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.math.Vector4f;

/**
 * Everything outside the window is culled and not drawn onto the screen
 */
public class AWindowDrawable extends AAggregateDrawable {
	private static final ThreadLocal<Vector4f> VECTOR_POOL = new ThreadLocal<>();
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "window"), AWindowDrawable::new);
	protected final int width, height;
	protected final boolean optimizeWithBounds;

	protected final Collection<ADrawable> actuallyDraw;

	/**
	 * @param optimizeWithBounds true if the window should use the bounds of the drawable to determine whether or not to render it
	 */
	public AWindowDrawable(int width, int height, boolean optimizeWithBounds) {
		this(ENTRY, width, height, optimizeWithBounds);
	}

	protected AWindowDrawable(DrawableRegistry.@Nullable Entry id, int width, int height, boolean bounds) {
		super(id);
		this.width = width;
		this.height = height;
		this.optimizeWithBounds = bounds;
		this.actuallyDraw = null;
	}

	protected AWindowDrawable(DrawableRegistry.Entry entry, NBTagView view) {
		super(entry, view);
		this.width = view.getInt("width");
		this.height = view.getInt("height");
		this.optimizeWithBounds = view.getBool("optimizeWithBounds");
		if(this.optimizeWithBounds) {
			this.actuallyDraw = new HashSet<>();
		} else {
			this.actuallyDraw = this.drawables;
		}
	}

	@Override
	protected boolean onSync(ADrawable drawable) {
		if(this.isClient() && this.optimizeWithBounds) {
			this.compute(drawable, false);
			drawable.addBoundsChangeListener((drawable1, old, current) -> {
				this.compute(drawable1, true);
			});
			drawable.addTransformationChangeListener((drawable1, old, current) -> {
				this.compute(drawable1, true);
			});
		}
		return super.onSync(drawable);
	}

	private void compute(ADrawable drawable, boolean remove) {
		Polygon enclosing = drawable.getBounds().getEnclosing();
		Vector4f v4f = VECTOR_POOL.get();
		if(v4f == null) {
			VECTOR_POOL.set(v4f = new Vector4f(0, 0, 0, 0)); // todo use withInitial
		}
		v4f.set(this.width, this.height, 1, 1);
		v4f.transform(drawable.getInvertedMatrix());
		float minX = enclosing.getX(0), minY = enclosing.getY(0);
		if(minX > v4f.getX() || minY > v4f.getY()) {
			if(remove) {
				this.actuallyDraw.remove(drawable);
			}
		} else {
			Polygon transformed = drawable.getBounds().toBuilder().transform(drawable.getTransformation()).build().getEnclosing();
			minX = transformed.getX(2);
			minY = transformed.getY(2);
			if(minX < 0 || minY < 0) {
				if(remove) {
					this.actuallyDraw.remove(drawable);
				}
			} else {
				this.actuallyDraw.add(drawable);
			}
		}
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		Stencil stencil = graphics.stencil();
		int stencilId = stencil.startStencil(Stencil.Type.TRACING);
		graphics.fillRect(this.width, this.height, 0xffffffff);
		stencil.fill(stencilId);
		for (ADrawable drawable : this.actuallyDraw) {
			drawable.render(container, graphics, tickDelta);
		}
		stencil.endStencil(stencilId);
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		super.write0(container, output);
		output.putInt("width", this.width);
		output.putInt("height", this.height);
		output.putBool("optimizeWithBounds", this.optimizeWithBounds);
	}
}
