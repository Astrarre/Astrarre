package io.github.astrarre.hash.v0.api;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import com.google.common.hash.Funnel;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtNull;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class AbstractHasher implements Hasher {
	private static final byte[] EMPTY = "empty".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] NULL = "null".getBytes(StandardCharsets.US_ASCII);

	final ByteBuffer buf = ByteBuffer.allocate(8);
	protected long version;
	OutputStream stream;

	@Override
	public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
		funnel.funnel(instance, this);
		return this;
	}

	@Override
	public Hasher putItemStack(ItemStack stack) {
		this.putItemStackIgnoreCount(stack);
		this.putInt(stack.getCount());
		return this;
	}

	@Override
	public Hasher putItemExact(Item item) {
		this.putRegistry(item, Registry.ITEM);
		this.putNbt((NbtElement) null);
		return null;
	}

	@Override
	public Hasher putItemStackIgnoreCount(ItemStack stack) {
		this.putRegistry(stack.getItem(), Registry.ITEM);
		this.putNbt(stack.getNbt());
		return this;
	}

	@Override
	public Hasher putItemKey(ItemKey key) {
		this.putRegistry(key.getItem(), Registry.ITEM);
		this.putNbt(key.getTag());
		return null;
	}

	@Override
	public Hasher putNbt(NbtElement element) {
		if(element == null) {
			return this.putBytes(EMPTY);
		} else if(element instanceof NbtCompound n) {
			if(n.isEmpty()) {
				return this.putBytes(EMPTY);
			} else {
				for(String key : (Iterable<String>) () -> n.getKeys().stream().sorted().iterator()) {
					this.putPackableString(key, StandardCharsets.UTF_8);
					this.putNbt(n.get(key));
				}
				return this;
			}
		} else if(element instanceof NbtByteArray bs) {
			byte[] arr = bs.getByteArray();
			for(int i = 0; i < bs.size(); i++) {
				this.putInt(i);
				this.putByte(arr[i]);
			}
			return this;
		} else if(element instanceof NbtIntArray is) {
			int[] arr = is.getIntArray();
			for(int i = 0; i < is.size(); i++) {
				this.putInt(i);
				this.putInt(arr[i]);
			}
			return this;
		} else if(element instanceof NbtLongArray ls) {
			long[] arr = ls.getLongArray();
			for(int i = 0; i < ls.size(); i++) {
				this.putInt(i);
				this.putLong(arr[i]);
			}
			return this;
		} else if(element instanceof AbstractNbtList<?> l) {
			for(int i = 0, size = l.size(); i < size; i++) {
				NbtElement e = l.get(i);
				this.putInt(i);
				this.putNbt(e);
			}
			return this;
		} else if(element instanceof NbtByte b) {
			return this.putByte(b.byteValue());
		} else if(element instanceof NbtShort s) {
			return this.putShort(s.shortValue());
		} else if(element instanceof NbtInt i) {
			return this.putInt(i.intValue());
		} else if(element instanceof NbtFloat f) {
			return this.putFloat(f.floatValue());
		} else if(element instanceof NbtLong l) {
			return this.putLong(l.longValue());
		} else if(element instanceof NbtDouble d) {
			return this.putDouble(d.doubleValue());
		} else if(element instanceof NbtNull) {
			return this.putBytes(NULL);
		} else if(element instanceof NbtString s) {
			return this.putString(s.asString(), StandardCharsets.UTF_8);
		} else {
			throw new UnsupportedOperationException("unknown type " + element.getClass());
		}
	}

	@Override
	public Hasher putNbt(NbtValue value) {
		return this.putNbt(Validate.transform(value, NbtValue::asMinecraft));
	}

	@Override
	public <T> Hasher putRegistry(T object, Registry<T> registry) {
		return this.putIdentifier(registry.getId(object));
	}

	// do packing for sbeed
	@Override
	public Hasher putIdentifier(Identifier id) {
		return this.putIdentifier(id.getNamespace(), id.getPath());
	}

	@Override
	public Hasher putIdentifier(String namespace, String path) {
		this.putPackableString(namespace, StandardCharsets.US_ASCII);
		this.putPackableString(path, StandardCharsets.US_ASCII);
		return this;
	}

	@Override
	public Hasher putIdentifier(Id id) {
		return this.putIdentifier(id.mod(), id.path());
	}

	@Override
	public Hasher putBlockState(BlockState state) {
		this.putRegistry(state.getBlock(), Registry.BLOCK);
		state.getProperties().stream().sorted(Comparator.comparing(Property::getName)).forEachOrdered(property -> this.putProperty(state, property));
		return this;
	}

	@Override
	public <T extends Comparable<T>> Hasher putProperty(BlockState state, Property<T> property) {
		T val = state.get(property);
		this.putPackableString(property.getName(), StandardCharsets.UTF_8);
		this.putPackableString(property.name(val), StandardCharsets.UTF_8);
		return this;
	}

	@Override
	public Hasher putByte(byte b) {
		version++;
		this.putByte0(b);
		return this;
	}

	@Override
	public Hasher putBytes(byte[] bytes) {
		version++;
		return this.putBytes(bytes, 0, bytes.length);
	}

	@Override
	public Hasher putBytes(byte[] bytes, int off, int len) {
		version++;
		this.putBytes0(bytes, off, len);
		return this;
	}

	@Override
	public Hasher putShort(short s) {
		version++;
		this.buf.putShort(s);
		return this.hash();
	}

	@Override
	public Hasher putInt(int i) {
		version++;
		this.buf.putInt(i);
		return this.hash();
	}

	@Override
	public Hasher putLong(long l) {
		version++;
		this.buf.putLong(l);
		return this.hash();
	}

	@Override
	public Hasher putFloat(float f) {
		version++;
		this.buf.putFloat(f);
		return this.hash();
	}

	@Override
	public Hasher putDouble(double d) {
		version++;
		this.buf.putDouble(d);
		return this.hash();
	}

	@Override
	public Hasher putBoolean(boolean b) {
		version++;
		return this.putByte((byte) (b ? 1 : 0));
	}

	@Override
	public Hasher putChar(char c) {
		version++;
		this.buf.putChar(c);
		return this.hash();
	}

	@Override
	public Hasher putUnencodedChars(CharSequence seq) {
		version++;
		for(int i = 0; i < seq.length(); i += 4) {
			this.buf.putChar(seq.charAt(i));
			this.buf.putChar(seq.charAt(i + 1));
			this.buf.putChar(seq.charAt(i + 2));
			this.buf.putChar(seq.charAt(i + 3));
			this.hash();
		}
		return this;
	}

	@Override
	public Hasher putString(CharSequence seq, Charset charset) {
		version++;
		CharBuffer buffer = CharBuffer.wrap(seq);
		CharsetEncoder encoder = charset.newEncoder();
		while(encoder.encode(buffer, this.buf, true) == CoderResult.OVERFLOW) {
			this.hash();
		}
		this.hash();
		return this;
	}

	@Override
	public long getVersion() {
		return this.version;
	}

	@Override
	public OutputStream asOutputStream() {
		OutputStream stream = this.stream;
		if(stream == null) {
			this.stream = stream = this.createOutputStream0();
		}
		return stream;
	}

	protected abstract void putByte0(byte b);

	protected abstract void putBytes0(byte[] bytes, int off, int len);

	protected OutputStream createOutputStream0() {
		return new OutputStream() {
			@Override
			public void write(int b) {
				AbstractHasher.this.putByte((byte) b);
			}

			@Override
			public void write(byte[] b, int off, int len) {
				AbstractHasher.this.putBytes(b, off, len);
			}
		};
	}

	private Hasher hash() {
		ByteBuffer buffer = this.buf;
		try {
			this.putBytes(buffer.array(), 0, buffer.position());
		} finally {
			this.buf.clear();
		}
		return this;
	}
}
