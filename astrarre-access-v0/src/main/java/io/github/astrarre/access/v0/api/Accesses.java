package io.github.astrarre.access.v0.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

public final class Accesses {
	private static final Map<Entry, Access<?>> ACCESSES = new HashMap<>();
	private Accesses() {}

	public static <A extends Access<F>, F> A getOrRegister(TypeToken<A> token, String modid, String namespace, Supplier<A> supplier) {
		return (A) ACCESSES.computeIfAbsent(new Entry(modid, namespace, token), entry -> supplier.get());
	}

	@Nullable
	public static <A extends Access<F>, F> A get(TypeToken<A> token, String modid, String namespace) {
		return (A) ACCESSES.get(new Entry(modid, namespace, token));
	}

	private final static class Entry {
		private final String modid, namespace;
		private final TypeToken<?> token;

		private Entry(String modid, String namespace, TypeToken<?> token) {
			this.modid = modid;
			this.namespace = namespace;
			this.token = token;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Entry)) {
				return false;
			}

			Entry entry = (Entry) o;

			if (!Objects.equals(this.modid, entry.modid)) {
				return false;
			}
			if (!Objects.equals(this.namespace, entry.namespace)) {
				return false;
			}
			return Objects.equals(this.token, entry.token);
		}

		@Override
		public int hashCode() {
			int result = this.modid != null ? this.modid.hashCode() : 0;
			result = 31 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
			result = 31 * result + (this.token != null ? this.token.hashCode() : 0);
			return result;
		}
	}
}
