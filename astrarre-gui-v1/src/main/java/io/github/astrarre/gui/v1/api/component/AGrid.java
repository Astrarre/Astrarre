package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.gui.v1.api.util.Transformed;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

public class AGrid extends ATransformingPanel {
	/**
	 * the size of each cell in the grid
	 */
	public final float cellWidth, cellHeight;
	/**
	 * how many cells on each axis to place
	 */
	public final int cellsX, cellsY;

	protected int index;

	@Override
	protected Transformed<?> transform(Transformed<?> current, float cw, float ch) {
		int i = this.index++;
		int cellX = i % this.cellsX, cellY = i / this.cellsX;
		if(cellY >= this.cellsY) {
			throw new IndexOutOfBoundsException(i + " >= " + (this.cellsX * this.cellsY));
		}

		Transform3d transform = Transform3d.translate(cellX * this.cellWidth, cellY * this.cellHeight, 0);
		return current.before(transform);
	}

	public APanel add(AComponent c, int cellX, int cellY) {
		return this.add(c, c.getWidth(), c.getHeight(), cellX, cellY);
	}

	public APanel add(AComponent component, float width, float height, int cellX, int cellY) {
		int i = this.index;
		this.index = cellX * this.cellsX + cellY;
		this.add(component, width, height);
		this.index = i;
		return this;
	}

	public AGrid(int cellsX, int cellsY) {
		this(16, cellsX, cellsY);
	}

	public AGrid(float cellSize, int cellsX, int cellsY) {
		this(0, 0, cellSize, cellSize, cellsX, cellsY);
	}

	public AGrid(float cellWidth, float cellHeight, int cellsX, int cellsY) {
		this(0, 0, cellWidth, cellHeight, cellsX, cellsY);
	}

	public AGrid(float seperation, float cellWidth, float cellHeight, int cellsX, int cellsY) {
		this(seperation, seperation, cellWidth, cellHeight, cellsX, cellsY);
	}

	public AGrid(float seperationX, float seperationY, float cellWidth, float cellHeight, int cellsX, int cellsY) {
		this.cellWidth = cellWidth + seperationX;
		this.cellHeight = cellHeight + seperationY;
		this.cellsX = cellsX;
		this.cellsY = cellsY;
		this.lockBounds(false);
		this.setBounds((cellWidth + seperationX) * cellsX - seperationX, (cellHeight + seperationY) * cellsY - seperationY);
		this.lockBounds(true);
	}

}
