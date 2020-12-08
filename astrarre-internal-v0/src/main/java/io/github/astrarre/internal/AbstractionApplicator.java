package io.github.astrarre.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.chocohead.mm.api.ClassTinkerers;

public class AbstractionApplicator implements Runnable {
	public static final Properties PROPERTIES = read(AbstractionApplicator.class.getResourceAsStream("/manifest.properties"));

	@Override
	public void run() {
		PROPERTIES.forEach((k, v) -> ClassTinkerers.addTransformation((String) k, c -> c.interfaces.add((String) v)));
	}

	private static Properties read(InputStream reader) {
		Properties properties = new Properties();
		try {
			properties.load(reader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return properties;
	}
}
