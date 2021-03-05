package io.github.astrarre.recipies.internal;

import java.io.IOException;
import java.io.Reader;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import org.jetbrains.annotations.NotNull;

public final class RecordingReader extends Reader {
	public final Reader reader;
	public final CharArrayList chars = new CharArrayList();

	public RecordingReader(Reader reader) {this.reader = reader;}

	@Override
	public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
		int read = this.reader.read(cbuf, off, len);
		if(read != -1) {
			this.chars.addElements(this.chars.size(), cbuf, off, read);
		}
		return read;
	}

	@Override
	public void close() throws IOException {
		this.reader.close();
	}
}
