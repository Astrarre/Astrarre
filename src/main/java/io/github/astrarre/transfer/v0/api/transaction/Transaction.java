package io.github.astrarre.transfer.v0.api.transaction;

// todo some way of de-duplicating containers
public final class Transaction implements AutoCloseable {
	private static final ThreadLocal<Transaction> ACTIVE = new ThreadLocal<>();
	private final Transaction parent;
	private final int nest;
	private final boolean intent;
	// this is composited with andThen
	private Key compositeKey;

	/**
	 * <code>
	 * new Transaction(true)
	 * </code>
	 */
	public Transaction() {
		this(true);
	}

	/**
	 * @param intent {@link AutoCloseable#close()} will commit/abort the transaction if it has not been invalidated already. (true = commit, false =
	 * 		abort)
	 */
	public Transaction(boolean intent) {
		this.intent = intent;
		this.parent = ACTIVE.get();
		if (this.parent == null) {
			this.nest = 0;
		} else {
			this.nest = this.parent.nest + 1;
		}
		ACTIVE.set(this);
	}

	/**
	 * @deprecated playing with fire! This should only be called from Key
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
				public void onApply(Transaction transaction) {
					this.old.onApply(transaction);
					key.onApply(transaction);
				}

				@Override
				public void onAbort(Transaction transaction) {
					this.old.onAbort(transaction);
					key.onAbort(transaction);
				}
			};
		}
	}

	public Transaction getParent() {
		return this.parent;
	}

	public int getNestLevel() {
		return this.nest;
	}

	@Override
	public void close() {
		if (ACTIVE.get() == this) {
			if (this.intent) {
				this.commit();
			} else {
				this.abort();
			}
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
}
