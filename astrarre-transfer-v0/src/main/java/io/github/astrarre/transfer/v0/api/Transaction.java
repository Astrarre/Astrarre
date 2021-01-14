package io.github.astrarre.transfer.v0.api;

public final class Transaction {
	private static final ThreadLocal<Transaction> ACTIVE = new ThreadLocal<>();
	private final Transaction parent;
	private final int nest;
	// this is composited with andThen
	private Key compositeKey;

	public Transaction() {
		this.parent = ACTIVE.get();
		if (this.parent == null) {
			this.nest = 0;
		} else {
			this.nest = this.parent.nest + 1;
		}
		ACTIVE.set(this);
	}

	public <T> T get(Key.Object<T> key) {
		return key.get(this);
	}

	public int get(Key.Int key) {
		return key.get(this);
	}

	public long get(Key.Long key) {
		return key.get(this);
	}

	public float get(Key.Float key) {
		return key.get(this);
	}

	public double get(Key.Double key) {
		return key.get(this);
	}

	public <T> void set(Key.Object<T> key, T value) {
		if (key.store(this, value)) {
			if (key.store(this, value)) {
				this.enlistKey(key);
			}
		}
	}

	/**
	 * @deprecated playing with fire! If you want to make a Key for short or other primitives, you can use this
	 */
	@Deprecated
	@SuppressWarnings ("DeprecatedIsStillUsed")
	public void enlistKey(Key key) {
		if (this.compositeKey == null) {
			this.compositeKey = key;
		} else {
			this.compositeKey = new Key() {
				private final Key old = Transaction.this.compositeKey;

				@Override
				protected void onApply(Transaction transaction) {
					this.old.onApply(transaction);
					key.onApply(transaction);
				}

				@Override
				protected void onAbort(Transaction transaction) {
					this.old.onAbort(transaction);
					key.onAbort(transaction);
				}
			};
		}
	}

	public void set(Key.Int key, int value) {
		if (key.store(this, value)) {
			this.enlistKey(key);
		}
	}

	public void set(Key.Long key, long value) {
		if (key.store(this, value)) {
			this.enlistKey(key);
		}
	}

	public void set(Key.Float key, float value) {
		if (key.store(this, value)) {
			this.enlistKey(key);
		}
	}

	public void set(Key.Double key, double value) {
		if (key.store(this, value)) {
			this.enlistKey(key);
		}
	}

	public void abort() {
		this.validateThread(
				"Transaction must be invalidated on the same thread it was created on, and you cannot abort a transaction without invalidating it's "
				+ "children!");
		if (this.compositeKey != null) {
			this.compositeKey.onAbort(this);
		}
		ACTIVE.set(this.parent);
	}

	/**
	 * ensures that the transaction is on the correct thread, and is the active transaction
	 *
	 * @param err the error message
	 * @throws IllegalStateException if false
	 */
	public void validateThread(String err) {
		if (ACTIVE.get() != this) {
			throw new IllegalStateException(err);
		}
	}

	public void commit() {
		this.validateThread(
				"Transaction must be invalidated on the same thread it was created on, and you cannot commit a transaction without invalidating " +
				"it's" + " children!");
		if (this.compositeKey != null) {
			this.compositeKey.onApply(this);
		}
		ACTIVE.set(this.parent);
	}

	public Transaction getParent() {
		return this.parent;
	}

	public int getNestLevel() {
		return this.nest;
	}
}
