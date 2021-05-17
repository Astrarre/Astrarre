package io.github.astrarre.transfer.v0.api.transaction;


import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.Nullable;

/**
 * a 'serialized world state' more documentation on the concept can be seen todo here
 */
public final class Transaction implements AutoCloseable {
	/**
	 * the 'global' transaction state
	 */
	public static final Transaction GLOBAL = null;

	private static final ThreadLocal<Transaction> ACTIVE = new ThreadLocal<>();
	private final Throwable debug;
	private final Transaction parent;
	private final int nest;
	private final boolean intent;
	// this is composited with andThen
	private List<Key> keys;
	private boolean invalidated = false;

	private Transaction(boolean intent) {
		this.intent = intent;
		this.parent = ACTIVE.get();
		if (this.parent == null) {
			this.nest = 0;
		} else {
			this.nest = this.parent.nest + 1;
		}
		ACTIVE.set(this);
		if(Validate.IS_DEV) {
			this.debug = new Throwable();
		} else {
			this.debug = null;
		}
	}

	/**
	 * <code>
	 * new Transaction(true)
	 * </code>
	 */
	public static Transaction create() {
		return create(true);
	}

	/**
	 * @param intent {@link AutoCloseable#close()} will commit/abort the transaction if it has not been invalidated already. (true = commit, false =
	 * 		abort)
	 */
	public static Transaction create(boolean intent) {
		return new Transaction(intent);
	}

	/**
	 * @return the current transaction
	 */
	@Nullable
	public static Transaction active() {
		return ACTIVE.get();
	}

	/**
	 * @deprecated playing with fire! This should only be called from Key
	 */
	@Deprecated
	@SuppressWarnings ("DeprecatedIsStillUsed")
	public void enlistKey(Key key) {
		if(this.keys == null) {
			this.keys = new ArrayList<>();
		}
		this.keys.add(key);
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
		this.invalidate(Key::onApply, "commit");
	}

	public void abort() {
		this.invalidate(Key::onAbort, "abort");
	}

	protected void invalidate(BiConsumer<Key, Transaction> func, String name) {
		if(this.invalidated) {
			String msg = "Cannot invalidate the same transaction twice!";
			if (!Validate.IS_DEV) {
				msg += "(use '-Dastrarre-enable-debug' to see where transaction was created)";
			}
			this.throwError(new IllegalStateException(msg));
		}
		this.invalidated = true;
		this.validateThread(
				"Transaction must be invalidated on the same thread it was created on, and you cannot " + name + " a transaction without invalidating it's "
				+ "children!");
		ACTIVE.set(this.parent);
		if(this.keys != null) {
			for (Key key : this.keys) {
				func.accept(key, this);
			}
		}
	}

	/**
	 * ensures that the transaction is on the correct thread, and is the active transaction
	 *
	 * @param err the error message
	 * @throws IllegalStateException if false
	 */
	public void validateThread(String err) {
		if (ACTIVE.get() != this) {
			if(!Validate.IS_DEV) {
				err += "(use '-Dastrarre-enable-debug' to see where transaction was created)";
			}
			this.throwError(new IllegalStateException(err));
		}
	}

	public <T extends Throwable> void throwError(T exception) throws T {
		if(this.debug != null) {
			TransactionInitialization debugException = new TransactionInitialization();
			debugException.setStackTrace(this.debug.getStackTrace());
			exception.initCause(debugException);
			throw exception;
		}
		throw exception;
	}

	/**
	 * @return the stacktrace of where the transaction was initialized, or null if {@link Validate#IS_DEV} is false
	 * @see Validate#IS_DEV
	 */
	@Nullable
	public StackTraceElement[] getInitializationStacktrace() {
		if(this.debug == null) {
			return null;
		}
		return this.debug.getStackTrace().clone();
	}

	/**
	 * @return an exception who's stacktrace is where the transaction was initialized, or null if {@link Validate#IS_DEV} is false
	 * @see Validate#IS_DEV
	 */
	@Nullable
	public TransactionInitialization getInitialization() {
		if(this.debug == null) {
			return null;
		}

		TransactionInitialization debugException = new TransactionInitialization();
		debugException.setStackTrace(this.debug.getStackTrace());
		return debugException;
	}

	public Transaction nest(boolean intent) {
		return Transaction.create(intent);
	}

	public Transaction nest() {
		return Transaction.create();
	}

	private static final class TransactionInitialization extends Throwable {
		public TransactionInitialization() {
			super("Transaction initialization stacktrace");
		}

	}
}
