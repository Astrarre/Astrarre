package io.github.astrarre.recipies.v0.api.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;

public final class CharInput extends Reader {
	private final Reader delegate;
	public CharInput(@NotNull Reader delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	@Override
	public int read(@NotNull CharBuffer target) {
		try {
			return this.delegate.read(target);
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public int read() {
		try {
			return this.delegate.read();
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public int read(char[] cbuf) {
		try {
			return this.delegate.read(cbuf);
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public long skip(long n) {
		try {
			return this.delegate.skip(n);
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public boolean ready() {
		try {
			return this.delegate.ready();
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public boolean markSupported() {
		return this.delegate.markSupported();
	}

	@Override
	public void mark(int readAheadLimit) {
		try {
			this.delegate.mark(readAheadLimit);
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public void reset() {
		try {
			this.delegate.reset();
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public int read(char[] cbuf, int off, int len) {
		try {
			return this.delegate.read(cbuf, off, len);
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public void close() {
		try {
			this.delegate.close();
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}
}
