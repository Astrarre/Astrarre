package io.github.astrarre.util.internal;

import io.github.astrarre.util.v0.api.Id;

public class IdentifierImpl implements Id {
	private final String mod, path;

	public IdentifierImpl(String mod, String path) {
		this.mod = mod;
		this.path = path;
	}

	@Override
	public String mod() {
		return this.mod;
	}

	@Override
	public String path() {
		return this.path;
	}

}
