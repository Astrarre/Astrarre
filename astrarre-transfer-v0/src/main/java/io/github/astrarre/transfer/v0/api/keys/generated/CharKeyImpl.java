package io.github.astrarre.transfer.v0.api.keys.generated;
// this class is autogenerated
// @formatter:off

import io.github.astrarre.transfer.v0.api.TransactionHandler;
import io.github.astrarre.transfer.v0.api.Key;
import io.github.astrarre.transfer.v0.api.Transaction;
import it.unimi.dsi.fastutil.chars.CharArrayList;

public class CharKeyImpl extends Key.Char {
	private final TransactionHandler handler;
	private final CharArrayList values = new CharArrayList();

	public CharKeyImpl(TransactionHandler handler, char originalValue) {
		this.handler = handler;
		this.values.add(0, originalValue);
	}

	public CharKeyImpl(char originalValue) {this(new TransactionHandler(), originalValue);}

	@Override
	public char get(Transaction transaction) {
		return this.values.peekChar(0);
	}

	@Override
	protected void onApply(Transaction transaction) {
		this.handler.pop(transaction);
		Transaction parent = transaction.getParent();
		this.set(parent, this.values.popChar());
	}

	@Override
	protected void onAbort(Transaction transaction) {
		this.values.popChar();
		this.handler.pop(transaction);
	}

	@Override
	public void set(Transaction transaction, char val) {
		if (this.handler.store(transaction)) {
			this.values.set(this.values.size() - 1, val);
		} else {
			this.values.push(val);
			transaction.enlistKey(this);
		}
	}
}
