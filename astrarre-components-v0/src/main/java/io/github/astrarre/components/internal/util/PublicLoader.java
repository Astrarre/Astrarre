package io.github.astrarre.components.internal.util;

import java.security.ProtectionDomain;

public final class PublicLoader extends ClassLoader {

	public PublicLoader(ClassLoader parent) {
		super(parent);
	}

	public Class<?> defineCls(String name, byte[] code, int off, int len, ProtectionDomain domain) {
		return this.defineClass(name, code, off, len, domain);
	}
}
