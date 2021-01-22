package clsgolf;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import net.fabricmc.loader.launch.knot.KnotClient;

public class ClasspathTestRunner extends BlockJUnit4ClassRunner {

	static ClassLoader customClassLoader;
	static {
		KnotClient.main(new String[] {});
		customClassLoader = Thread.currentThread().getContextClassLoader();
		System.out.println(customClassLoader);
	}

	public ClasspathTestRunner(Class<?> clazz) throws InitializationError {
		super(loadFromCustomClassloader(clazz));
	}

	// Loads a class in the custom classloader
	private static Class<?> loadFromCustomClassloader(Class<?> clazz) throws InitializationError {
		try {
			return Class.forName(clazz.getName(), true, customClassLoader);
		} catch (ClassNotFoundException e) {
			throw new InitializationError(e);
		}
	}


	// Runs junit tests in a separate thread using the custom class loader
	@Override
	public void run(final RunNotifier notifier) {
		Runnable runnable = () -> super.run(notifier);
		Thread thread = new Thread(runnable);
		thread.setContextClassLoader(customClassLoader);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}