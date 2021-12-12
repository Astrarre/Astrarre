package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v1.api.util.TransformedComponent;
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

	@Override
	protected List<TransformedComponent> transformAll(List<TransformedComponent> originalComponents) {
		int index = 0;
		List<TransformedComponent> components = new ArrayList<>(originalComponents.size());
		for(TransformedComponent current : originalComponents) {
			int i = index++;
			int cellX = i % this.cellsX, cellY = i / this.cellsX;
			if(cellY >= this.cellsY) {
				throw new IndexOutOfBoundsException(i + " >= " + (this.cellsX * this.cellsY));
			}

			Transform3d transform = Transform3d.translate(cellX * this.cellWidth, cellY * this.cellHeight, 0);
			components.add(current.before(transform));
		}
		return components;
	}

}
