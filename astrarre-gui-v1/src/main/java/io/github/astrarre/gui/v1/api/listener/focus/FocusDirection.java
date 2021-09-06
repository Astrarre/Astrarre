package io.github.astrarre.gui.v1.api.listener.focus;

public enum FocusDirection {
	/**
	 * focus on the next element in this panel (eg. tab)
	 */
	FORWARD(1),
	/**
	 * focus on the previous element in this panel (eg. ctrl+tab?)
	 */
	BACKWARDS(-1);

	public final int dir;

	FocusDirection(int dir) {this.dir = dir;}

	public boolean isForward() {
		return this == FORWARD;
	}
}
