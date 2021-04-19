package io.github.astrarre.transfer.v0.lba.keys;

import alexiil.mc.lib.attributes.misc.Reference;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;

public class ReferenceKey<T> extends ObjectKeyImpl<T> {
	public final Reference<T> reference;

	public ReferenceKey(Reference<T> reference) {
		this.reference = reference;
	}

	@Override
	protected T getRootValue() {
		return this.reference.get();
	}

	@Override
	protected void setRootValue(T val) {
		this.reference.set(val);
	}
}