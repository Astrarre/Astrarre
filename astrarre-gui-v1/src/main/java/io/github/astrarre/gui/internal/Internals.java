package io.github.astrarre.gui.internal;

/**
 * An interesting hack to prohibit the use of internals
 * Obviously nothing stops people from using reflection, but if people want to shoot themselves in the foot, good on em I guess
 */
public class Internals {
	static final Internals INSTANCE = new Internals();

	private Internals() {
	}
}
