package io.github.astrarre.gui.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import io.github.astrarre.gui.v0.api.Container;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.jetbrains.annotations.Nullable;

public abstract class ContainerInternal implements Container {
	private static final Random RANDOM = new Random();
	protected final List<Drawable> toDraw = new ArrayList<>();
	private final Object2IntOpenHashMap<Drawable> componentRegistry = new Object2IntOpenHashMap<>();
	private final Int2ObjectOpenHashMap<Drawable> reversedRegistry = new Int2ObjectOpenHashMap<>();
	private int nextId = RANDOM.nextInt(), nextClientId = this.nextId ^ 0xaaaaaaaa;

	@Override
	public void add(Drawable drawable) {
		Validate.isTrue(drawable.container == this, "Tried to add Drawable that already belongs to a container!");
		this.toDraw.add(drawable);
	}

	public int addRoot(Drawable drawable) {
		int id = this.isClient() ? this.nextClientId++ : this.nextId++;
		this.componentRegistry.put(drawable, id);
		this.reversedRegistry.put(id, drawable);
		if (!this.isClient()) {
			for (NetworkMember viewer : this.getViewers()) {
				// todo serialize Drawable
			}
		}
		return id;
	}


	@Nullable
	public Drawable forId(int id) {
		return this.reversedRegistry.get(id);
	}

	public void onClose() {
		ObjectIterator<Drawable> iterator = this.componentRegistry.keySet().iterator();
		while (iterator.hasNext()) {
			Drawable drawable = iterator.next();
			drawable.remove();
			iterator.remove();
		}
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	public void read(Input input) {
		int size = input.readInt();
		for (int i = 0; i < size && input.bytes() > 0; i++) {
			Id id = input.readId();
			Drawable drawable = this.read(id, input);
			if (drawable == null) {
				throw new IllegalStateException("Broken (d/s)erializer! " + id);
			}
			this.reversedRegistry.put(drawable.getId(), drawable);
			this.componentRegistry.put(drawable, drawable.getId());
		}
	}

	@Nullable
	private Drawable read(Id id, Input input) {
		BiFunction<Container, Input, Drawable> function = DrawableRegistry.forId(id);
		if (function == null || input.bytes() < 4) {
			return null;
		} else {
			int syncId = input.readInt();
			Drawable drawable = function.apply(this, input);
			((DrawableInternal) drawable).id = syncId;
			return drawable;
		}
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	public void write(Output output) {
		output.writeInt(this.componentRegistry.size());
		for (Drawable drawable : this.componentRegistry.keySet()) {
			this.write(output, drawable);
		}
	}

	private void write(Output output, Drawable drawable) {
		output.writeId(drawable.registryId.id);
		output.writeInt(drawable.getId());
		drawable.write(output);
	}
}
