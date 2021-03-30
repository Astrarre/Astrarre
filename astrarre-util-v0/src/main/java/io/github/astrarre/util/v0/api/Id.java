package io.github.astrarre.util.v0.api;


import net.minecraft.util.Identifier;

/**
 * A small abstraction over Identifier with a shorter name
 *
 * overrides equals and hashcode
 */
public interface Id {
	static Id of(Identifier identifier) {
		return (Id) identifier;
	}

	static Id create(String id, String path) {
		return (Id) new Identifier(id, path);
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

	default Identifier to() {
		return (Identifier) this;
	}
}
