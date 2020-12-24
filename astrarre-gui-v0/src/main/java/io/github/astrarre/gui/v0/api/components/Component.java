package io.github.astrarre.gui.v0.api.components;

import io.github.astrarre.stripper.Hide;
import io.github.astrarre.gui.v0.api.annotations.ClientOnlyProperty;
import io.github.astrarre.gui.v0.api.Graphics2d;
import io.github.astrarre.gui.v0.api.util.Closeable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * something to draw on a screen
 */
public abstract class Component {
	/**
	 * the offset of the current component
	 */
	@ClientOnlyProperty
	public int x, y;

	private int id;

	/**
	 * @param tickDelta the 'fraction' of the tick that this is being rendered in.
	 */
	@Environment(EnvType.CLIENT)
	public final void render(Graphics2d g2d, float tickDelta) {
		int zLevel = g2d.getZ();
		g2d.setZ(zLevel + 1);
		try(Closeable ignored = g2d.setOffsetCloseable(this.x, this.y)) {
			this.render0(g2d, tickDelta);
		} finally {
			g2d.setZ(zLevel);
		}
	}

	@Environment(EnvType.CLIENT)
	protected abstract void render0(Graphics2d g2d, float tickDelta);

	public int getId() {
		return this.id;
	}

	@Hide
	public void setId(int id) {
		this.id = id;
	}
}
