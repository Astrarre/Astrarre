package io.github.astrarre.gui.v0.swing.adapter;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;

public abstract class ComponentAdapter extends Drawable implements Interactable {
	private BufferedImage image;
	private final JPanel panel;
	private final Component component;

	public ComponentAdapter(DrawableRegistry.Entry id, Component component) {
		super(id);
		this.image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
		JPanel panel = new JPanel() {
			@Override
			public void repaint(long tm, int x, int y, int width, int height) {
				this.printAll(ComponentAdapter.this.image.getGraphics());
			}
		};
		panel.setSize(component.getSize());
		panel.add(component);
		component.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = e.getComponent();
				ComponentAdapter.this.image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
				panel.setSize(c.getSize());
			}
		});
		this.panel = panel;
		this.component = component;
		panel.repaint();
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		for (int x = 0; x < this.image.getWidth(); x++) {
			for (int y = 0; y < this.image.getHeight(); y++) {
				graphics.fillRect(x, y, x+1, y+1, this.image.getRGB(x, y));
			}
		}
	}

	@Override
	protected void write0(RootContainer container, Output output) {

	}
}
