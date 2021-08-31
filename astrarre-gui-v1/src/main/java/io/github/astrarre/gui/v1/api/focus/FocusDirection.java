package io.github.astrarre.gui.v1.api.focus;

public enum FocusDirection {
	/**
	 * focus on the next element in this panel (eg. tab)
	 */
	FORWARD,
	/**
	 * focus on the previous element in this panel (eg. ctrl+tab?)
	 */
	BACKWARDS;

	public boolean isForward() {
		return this == FORWARD;
	}
}
