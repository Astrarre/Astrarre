package io.github.astrarre.rendering.v0.edge;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

/**
 * A stencil is like an IRL stencil, it's a template/shape that 'occludes' the 'paper' (screen) underneath so when you paint/draw over the whole
 * thing, only the part not occluded by the stencil is drawn on the paper below
 */
public class Stencil {
	/**
	 * the maximum nesting for stencils
	 */
	private static final int MAX_STENCIL = 255;
	private int stencilStart = 0;

	private static final Type[] TYPES = Type.values();
	public enum Type {
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
			GL11.glClearStencil(0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
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
