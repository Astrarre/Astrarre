package io.github.astrarre.gui.internal;

import java.util.Random;
import java.util.function.BiFunction;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.util.v0.api.Id;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.jetbrains.annotations.Nullable;

public abstract class RootContainerInternal implements RootContainer {
	private static final Random RANDOM = new Random();
	private final Object2IntOpenHashMap<Drawable> componentRegistry = new Object2IntOpenHashMap<>();
	private final Int2ObjectOpenHashMap<Drawable> reversedRegistry = new Int2ObjectOpenHashMap<>();
	protected final Panel panel;

	private int nextId = RANDOM.nextInt(), nextClientId = this.nextId + Integer.MAX_VALUE;

	protected RootContainerInternal() {
		this.panel = new Panel(this);
	}

	protected RootContainerInternal(Input input) {
		int size = input.readInt();
		for (int i = 0; i < size && input.bytes() > 0; i++) {
			Drawable drawable = readDrawable(this, input);
			this.addSynced(drawable);
		}
		this.panel = (Panel) Drawable.read(this, input);
	}

	@Override
	public Panel getContentPanel() {
		return this.panel;
	}

	int addRoot(Drawable drawable) {
		int id = this.isClient() ? this.nextClientId++ : this.nextId++;
		this.componentRegistry.put(drawable, id);
		this.reversedRegistry.put(id, drawable);
		if (!this.isClient()) {
			for (NetworkMember viewer : this.getViewers()) {
				viewer.send(GuiPacketHandler.ADD_DRAWABLE, output -> {
					output.writeEnum(this.getType());
					drawable.write(output);
				});
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
	void write(Output output) {
		output.writeInt(this.componentRegistry.size());
		for (Drawable drawable : this.componentRegistry.keySet()) {
			drawable.write(output);
		}
		this.panel.write(output);
	}

	/**
	 * id     | Identifer
	 * syncId | int
	 * ------------------
	 * rest of input
	 */
	public static Drawable readDrawable(RootContainer rootContainer, Input input) {
		Id id = input.readId();
		BiFunction<RootContainer, Input, Drawable> function = DrawableRegistry.forId(id);
		if (function == null || input.bytes() < 4) {
			throw new IllegalStateException("Broken (d/s)erializer!");
		} else {
			int syncId = input.readInt();
			Drawable drawable = function.apply(rootContainer, input);
			((DrawableInternal) drawable).id = syncId;
			return drawable;
		}
	}

	void addSynced(Drawable drawable) {
		this.componentRegistry.put(drawable, drawable.getSyncId());
		this.reversedRegistry.put(drawable.getSyncId(), drawable);
	}
}
