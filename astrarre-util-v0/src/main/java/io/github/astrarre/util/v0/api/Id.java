package io.github.astrarre.util.v0.api;


import io.github.astrarre.util.internal.FapiMixinPlugin;
import io.github.astrarre.util.internal.IdentifierImpl;

/**
 * A small abstraction over Identifier with a shorter name
 *
 * overrides equals and hashcode
 */
public interface Id {
	static Id create(String id, String path) {
		if(FapiMixinPlugin.FAPI) {
			return (Id) (Object) new io.github.minecraftcursedlegacy.api.registry.Id(id, path);
		}
		return new IdentifierImpl(id, path);
	}

	/**
	 * should actually be faster than mojang's version
	 */
	static Id create(String str) {
		int i = str.indexOf(':');
		if(i == -1) {
			return create("minecraft", str);
		}
		return create(str.substring(0, i), str.substring(i + 1));
	}

	String mod();
	String path();
}
