package io.github.astrarre.util.v0.api;

import io.github.astrarre.stripper.Hide;

import net.minecraft.util.Identifier;

/**
 * overrides equals and hashcode
 */
public interface Id {
	@Hide
	static Id of(Identifier identifier) {
		return (Id) identifier;
	}

	static Id newInstance(String id, String path) {
		return (Id) new Identifier(id, path);
	}

	/**
	 * should actually be faster than mojang's version
	 */
	static Id newInstance(String str) {
		int i = str.indexOf(':');
		if(i == -1) {
			return newInstance("minecraft", str);
		}
		return newInstance(str.substring(0, i), str.substring(i+1));
	}

	String id();
	String path();

	@Hide
	default Identifier to() {
		return (Identifier) this;
	}
}
