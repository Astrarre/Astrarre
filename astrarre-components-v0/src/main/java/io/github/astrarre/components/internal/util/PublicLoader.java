package io.github.astrarre.components.internal.util;

import java.security.ProtectionDomain;

public final class PublicLoader extends ClassLoader {
	public static final PublicLoader INSTANCE = new PublicLoader();

	public Class<?> defineCls(String name, byte[] code, int off, int len, ProtectionDomain domain) {
		return this.defineClass(name, code, off, len, domain);
	}
}
