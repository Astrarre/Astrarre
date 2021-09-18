package ex;

import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.listener.cursor.ClickType;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.rendering.v1.api.space.Render3d;

public class TestCustomComponent extends AComponent implements MouseListener {
	public TestCustomComponent() {
		this.setBounds(100, 100); // bounds are used by mouse listeners and aligners like AList
		this.lockBounds(true); // this locks the bounds, you can unlock it, but no one else can (the method is protected)
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		// ...
	}

	/**
	 * If your component's bounds is not rectangle, you can override this method to filter mouse events.
	 * However, you should still setBounds to the circumscribed rectangle around your real bounds because other components
	 * rely on that information
	 */
	@Override
	public boolean inBounds(float x, float y) {
		float cx = x - 50, cy = y - 50;
		return cx * cx + cy * cy <= 2500;
	}


	@Override
	public void mouseMoved(Cursor cursor, float deltaX, float deltaY) {
		MouseListener.super.mouseMoved(cursor, deltaX, deltaY);
	}

	@Override
	public boolean mouseClicked(Cursor cursor, ClickType type) {
		System.out.println("mouse clicked!");
		return MouseListener.super.mouseClicked(cursor, type);
	}

	@Override
	public boolean mouseReleased(Cursor cursor, ClickType type) {
		return MouseListener.super.mouseReleased(cursor, type);
	}

	@Override
	public boolean mouseDragged(Cursor cursor, ClickType type, float deltaX, float deltaY) {
		return MouseListener.super.mouseDragged(cursor, type, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(Cursor cursor, float scroll) {
		return MouseListener.super.mouseScrolled(cursor, scroll);
	}
}