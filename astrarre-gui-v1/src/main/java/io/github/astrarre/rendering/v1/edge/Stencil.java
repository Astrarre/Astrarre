package io.github.astrarre.rendering.v1.edge;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.util.v0.api.Edge;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

/**
 * A stencil is like an IRL stencil, it's a template/shape that 'occludes' the 'paper' (screen) underneath so when you paint/draw over the whole
 * thing, only the part not occluded by the stencil is drawn on the paper below.
 *
 * todo make this per renderer and make it auto flush before each stage so u don't need to do it manually
 */
@Edge
@ApiStatus.Experimental
public class Stencil {
	/**
	 * the maximum nesting for stencils
	 */
	private static final int MAX_STENCIL = 255;
	private int stencilStart = 0;

	private static final Type[] TYPES = Type.values();
	public enum Type {
		/**
		 * just traces the stencil, it doesn't actually render to the screen
		 */
		TRACING {
			@Override
			protected void open(int stencilId) {
				PASSTHROUGH.open(stencilId);
				RenderSystem.colorMask(false, false, false, false);
				RenderSystem.depthMask(false);
			}

			@Override
			protected void draw(int stencilId) {
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.depthMask(true);
				PASSTHROUGH.draw(stencilId);
			}
		},
		/**
		 * traces and actually renders to the screen
		 */
		PASSTHROUGH {
			@Override
			protected void open(int stencilId) {
				RenderSystem.stencilFunc(GL11.GL_NEVER, stencilId, 0xFF);
				RenderSystem.stencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
				RenderSystem.stencilMask(0xFF);
			}

			@Override
			protected void draw(int stencilId) {
				RenderSystem.stencilMask(0x00);
				RenderSystem.stencilFunc(GL11.GL_EQUAL, stencilId, 0xFF);
			}
		},
		/**
		 * traces, does not render, however when you paint over it, it renders everything *except* where the stencil exists
		 */
		TRACING_INVERTED {
			@Override
			protected void open(int stencilId) {
				TRACING.open(stencilId);
			}

			@Override
			protected void draw(int stencilId) {
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.depthMask(true);
				PASSTHROUGH_INVERTED.draw(stencilId);
			}
		},
		/**
		 * traces and renders, however when you paint over it, it renders everything *except* where the stencil exists
		 */
		PASSTHROUGH_INVERTED {
			@Override
			protected void open(int stencilId) {
				PASSTHROUGH.open(stencilId);
			}

			@Override
			protected void draw(int stencilId) {
				RenderSystem.stencilMask(0x00);
				RenderSystem.stencilFunc(GL11.GL_NOTEQUAL, stencilId, 0xFF);
			}
		};

		protected abstract void open(int stencilId);
		protected abstract void draw(int stencilId);
	}

	Stencil() {}

	/**
	 * a passthrough stencil creates the stencil like normal, but it also draws to the screen
	 *
	 * @return a stencil id
	 */
	public int startStencil(Stencil.Type type) {
		int stencilId = ++this.stencilStart;
		if (stencilId > MAX_STENCIL) {
			throw new IllegalStateException("cannot make more nested stencils than " + MAX_STENCIL);
		}
		if (stencilId == 1) {
			RenderSystem.clearStencil(0);
			RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, false);
			GL11.glEnable(GL11.GL_STENCIL_TEST);
		}
		type.open(stencilId);
		return stencilId | type.ordinal() << 8;
	}

	/**
	 * draw whatever you want to draw over the stencil. Like an IRL stencil, you can paint whatever you want with no regards for the stencil underneath,
	 * and once the stencil is "taken away" you are left with a perfectly bounded shape
	 */
	public void fill(int stencilId) {
		int type = stencilId >>> 8;
		TYPES[type].draw(stencilId & 0xff);
	}


	/**
	 * end the stencil
	 */
	public void endStencil(int stencilId) {
		int id = stencilId & 0xff;
		if (this.stencilStart != id) {
			throw new IllegalStateException("Tried to end stencil before parents were ended");
		}
		this.stencilStart--;
		if (id == 1) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
	}

	/**
	 * @deprecated internal
	 */
	@Deprecated
	@ApiStatus.Internal
	@ApiStatus.Experimental
	public static Stencil newInstance() {
		return new Stencil();
	}
}