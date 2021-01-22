package clsgolf;

public class KnotCrashLoader extends ClassLoader {
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}
}
