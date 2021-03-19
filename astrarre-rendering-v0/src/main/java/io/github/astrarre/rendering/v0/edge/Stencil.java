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

	Stencil() {}

	/**
	 * A tracing stencil is a stencil that does not actually draw to the screen, it just creates a stencil For example, if you want to draw an
	 * silhouette of an item, you would 1) start a tracing stencil 2) draw the item 3) call drawTracingStencil 4) fill a black area 5) end the
	 * stencil
	 * starts a new stencil but blocks drawing to the screen
	 */
	public int startTracingStencil() {
		int stencilId = this.startPassthroughStencil();
		RenderSystem.colorMask(false, false, false, false);
		RenderSystem.depthMask(false);
		return stencilId;
	}

	/**
	 * a passthrough stencil creates the stencil like normal, but it also draws to the screen
	 *
	 * @return a stencil id
	 */
	public int startPassthroughStencil() {
		int stencilId = ++this.stencilStart;
		if (stencilId > MAX_STENCIL) {
			throw new IllegalStateException("cannot make more nested stencils than " + MAX_STENCIL);
		}
		if (stencilId == 1) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);
		}
		RenderSystem.stencilFunc(GL11.GL_NEVER, stencilId, 0xFF);
		RenderSystem.stencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
		RenderSystem.stencilMask(0xFF);
		return stencilId;
	}

	/**
	 * draw the desired object on the stencil.
	 */
	public void drawTracingStencil(int stencilId) {
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.depthMask(true);
		this.drawPassthroughStencil(stencilId);
	}

	public void drawInvertedTracingStencil(int stencilId) {
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.depthMask(true);
		this.drawInvertedPassthroughStencil(stencilId);
	}

	/**
	 * draw the desired object on the stencil
	 */
	public void drawPassthroughStencil(int stencilId) {
		RenderSystem.stencilMask(0x00);
		RenderSystem.stencilFunc(GL11.GL_EQUAL, stencilId, 0xFF);
	}

	public void drawInvertedPassthroughStencil(int stencilId) {
		RenderSystem.stencilMask(0x00);
		RenderSystem.stencilFunc(GL11.GL_NOTEQUAL, stencilId, 0xFF);
	}

	/**
	 * end the stencil
	 */
	public void endStencil(int stencilId) {
		if (this.stencilStart != stencilId) {
			throw new IllegalStateException("Tried to end stencil before parents were ended");
		}
		this.stencilStart--;
		if (stencilId == 1) {
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
