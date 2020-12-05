package io.github.astrarre.asm;

public class AbstractionApplicator implements Runnable {
	private static final int TEST = 4;
	public static final int NOT_TEST = internal();

	private static int internal() {
		return 0;
	}

	public static void publicApi() {
		// e
	}

	@Override
	public void run() {
		// todo implement MM, should this be in the root project?
	}
}
