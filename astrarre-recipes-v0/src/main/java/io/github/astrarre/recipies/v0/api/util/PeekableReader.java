package io.github.astrarre.recipies.v0.api.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import io.github.astrarre.recipies.internal.RecordingReader;
import io.github.astrarre.util.v0.api.Validate;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import org.jetbrains.annotations.NotNull;

public class PeekableReader extends Reader {
	public static final ThreadLocal<char[]> CHAR_BUFFERS = ThreadLocal.withInitial(() -> new char[1024]);
	private final Reader reader;
	private final CharArrayList peek = new CharArrayList();

	public PeekableReader(Reader reader) {
		this.reader = reader;
	}

	@Override
	public int read(@NotNull CharBuffer target) {
		if (target.hasArray()) {
			return this.read(target.array(), target.arrayOffset(), target.length());
		} else {
			try {
				return super.read(target);
			} catch (IOException e) {
				throw Validate.rethrow(e);
			}
		}
	}

	@Override
	public int read() {
		if (this.peek.isEmpty()) {
			try {
				return this.reader.read();
			} catch (IOException e) {
				throw Validate.rethrow(e);
			}
		} else {
			return this.peek.removeChar(0);
		}
	}

	@Override
	public int read(char[] cbuf) {
		return this.read(cbuf, 0, cbuf.length);
	}

	@Override
	public long skip(long n) {
		try {
			return super.skip(n);
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	@Override
	public void close() {
		try {
			this.reader.close();
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	/**
	 * @return the total amount of characters that have been peeked
	 */
	public int getPeeked() {
		return this.peek.size();
	}

	public String peekString(int chars) {
		if (chars > 1024) {
			throw new UnsupportedOperationException("cannot string peek ahead more than 1024 chars!");
		}

		char[] cbuf = CHAR_BUFFERS.get();
		int read = this.peek(cbuf, 0, chars);
		if (read == -1) {
			return "";
		}
		return new String(cbuf, 0, read);
	}

	@Override
	public int read(char[] cbuf, int off, int len) {
		try {
			if (this.peek.isEmpty()) {
				return this.reader.read(cbuf, off, len);
			} else if (this.peek.size() >= len) {
				this.peek.getElements(0, cbuf, off, len);
				this.peek.removeElements(0, len);
				return len;
			} else {
				int availablePeek = this.peek.size();
				int toRead = len - availablePeek;
				this.peek.getElements(0, cbuf, off, availablePeek);
				this.peek.clear();
				int read = this.reader.read(cbuf, off + availablePeek, toRead);
				if (read == -1) {
					return availablePeek;
				}
				return availablePeek + read;
			}
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	public int peek(char[] cbuf, int off, int len) {
		try {
			if (this.peek.isEmpty()) {
				int chars = this.reader.read(cbuf, off, len);
				if (chars == -1) {
					return -1;
				}
				this.peek.addElements(this.peek.size(), cbuf, off, chars);
				return chars;
			} else if (this.peek.size() >= len) {
				this.peek.getElements(0, cbuf, off, len);
				return len;
			} else {
				int availablePeek = this.peek.size();
				this.peek.getElements(0, cbuf, off, availablePeek);
				int read = this.reader.read(cbuf, off + availablePeek, len - availablePeek);
				if (read == -1) {
					return availablePeek;
				}
				this.peek.addElements(this.peek.size(), cbuf, off + availablePeek, read);
				return availablePeek + read;
			}
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}

	public int peek() {
		return this.peek(0);
	}

	public int peek(int off) {
		if (off < this.peek.size()) {
			return this.peek.getChar(off);
		} else {
			char[] buf = CHAR_BUFFERS.get();
			int val = this.peek(buf, 0, off+1);
			if (val != (off + 1)) {
				return -1;
			}
			return buf[off];
		}
	}

	public String readString(int chars) {
		if (chars > 1024) {
			throw new UnsupportedOperationException("cannot string peek ahead more than 1024 chars!");
		}

		char[] cbuf = CHAR_BUFFERS.get();
		int read = this.read(cbuf, 0, chars);
		if (read == -1) {
			return "";
		}
		return new String(cbuf, 0, read);
	}

	/**
	 * while there is an active (non-returned) sub reader, the current reader should not be touched!
	 *
	 * @return a new reader that can be returned or finished
	 */
	public PeekableReader createSubReader() {
		return new Sub(this);
	}

	public void abort(PeekableReader reader) {
		if (reader instanceof Sub) {
			RecordingReader r = (RecordingReader) reader.reader;
			this.peek.addAll(r.chars);
		} else {
			throw new UnsupportedOperationException("reader was not a sub reader!");
		}
	}

	public void commit(PeekableReader reader) {
		if (reader instanceof Sub) {
			this.peek.addAll(reader.peek);
		} else {
			throw new UnsupportedOperationException("reader was not a sub reader!");
		}
	}

	private static final class Sub extends PeekableReader {
		public Sub(Reader reader) {
			super(new RecordingReader(reader));
		}
	}
}
