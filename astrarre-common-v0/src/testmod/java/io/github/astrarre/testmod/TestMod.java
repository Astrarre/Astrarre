package io.github.astrarre.testmod;

import static java.lang.System.out;

import java.net.URI;
import java.net.URL;

import io.github.astrarre.v0.util.Id;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		try {
			Class<?> cls = this.getClass().getClassLoader().loadClass("io.github.astrarre.v0.util.Id");
			out.println(cls);
			out.println(cls.getClassLoader());

			URL url = Id.class.getResource("/io/github/astrarre/v0/util/Id.class");
			out.println(url);
			URI uri = url.toURI();
			out.println(uri);
			Id.newInstance("testmod", "identifier").getNamespace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
