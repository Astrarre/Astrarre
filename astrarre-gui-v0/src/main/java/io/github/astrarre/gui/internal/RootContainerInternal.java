package io.github.astrarre.gui.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.jetbrains.annotations.Nullable;

public abstract class RootContainerInternal implements RootContainer {
	static final AtomicInteger ID = new AtomicInteger(), CLIENT_ID = new AtomicInteger(Integer.MIN_VALUE);
	protected final List<Tickable> tickables = new ArrayList<>();
	protected final Panel panel;
	private final Object2IntOpenHashMap<Drawable> componentRegistry = new Object2IntOpenHashMap<>();
	private final Int2ObjectOpenHashMap<Drawable> reversedRegistry = new Int2ObjectOpenHashMap<>();
	int tick;
	private boolean reading;

	protected RootContainerInternal() {
		this.addRoot(this.panel = new Panel());
	}

	protected RootContainerInternal(Input input) {
		this(internal -> {}, input);
	}

	protected RootContainerInternal(Consumer<RootContainerInternal> toRun, Input input) {
		this.reading = true;
		toRun.accept(this);
		int size = input.readInt();
		for (int i = 0; i < size && input.bytes() > 0; i++) {
			Drawable drawable = Drawable.read(input);
			this.addSynced(drawable);
		}
		int panelId = input.readInt();
		this.reading = false;
		this.panel = (Panel) this.forId(panelId);
		for (Drawable value : this.reversedRegistry.values()) {
			((DrawableInternal) value).onAdded(this);
		}

	}

	void addSynced(Drawable drawable) {
		if (drawable instanceof Tickable) {
			this.tickables.add((Tickable) drawable);
		}

		this.componentRegistry.put(drawable, drawable.getSyncId());
		ObjectIterator<Int2ObjectMap.Entry<Drawable>> iterator = this.reversedRegistry.int2ObjectEntrySet().iterator();
		while (iterator.hasNext()) {
			Int2ObjectMap.Entry<Drawable> entry = iterator.next();
			if (entry.getValue() == drawable) {
				iterator.remove();
				break;
			}
		}
		this.reversedRegistry.put(drawable.getSyncId(), drawable);
		((DrawableInternal) drawable).rootsInternal.add(this);
		((DrawableInternal) drawable).setClient(this.isClient());
	}

	@Override
	public Panel getContentPanel() {
		return this.panel;
	}

	@Override
	public void addRoot(Drawable drawable) {
		if (!drawable.roots.isEmpty()) {
			if (drawable.isClient() != this.isClient()) {
				if (drawable.isClient()) {
					throw new IllegalArgumentException("Tried to add clientside component to serverside container!");
				} else {
					throw new IllegalArgumentException("Tried to add serverside component to clientside container!");
				}
			}
		} else if (this.componentRegistry.containsKey(drawable)) {
			return;
		}

		if (drawable instanceof Tickable) {
			this.tickables.add((Tickable) drawable);
		}

		int id = ((DrawableInternal) drawable).id;
		if (id == -1) {
			if (this.isClient()) {
				id = CLIENT_ID.incrementAndGet();
			} else {
				id = ID.incrementAndGet();
			}
			((DrawableInternal) drawable).id = id;
		}

		this.componentRegistry.put(drawable, id);
		this.reversedRegistry.put(id, drawable);
		((DrawableInternal) drawable).rootsInternal.add(this);
		((DrawableInternal) drawable).setClient(this.isClient());
		((DrawableInternal) drawable).onAdded(this);

		if (!this.isClient()) {
			NetworkMember member = this.getViewer();
			if (member != null) {
				member.send(GuiPacketHandler.ADD_DRAWABLE, output -> {
					output.writeEnum(this.getType());
					drawable.write(this, output);
				});
			}
		}
	}

	@Override
	public <T extends Drawable & Interactable> void setFocus(T drawable) {
		this.panel.setFocused(this, drawable, -1);
	}

	@Override
	@Nullable
	public Drawable forId(int id) {
		if (this.reading) {
			throw new IllegalStateException(
					"cannot check root container for id while it is being read (store the ids inside some internal buffer and query when you need " + "to)");
		}
		return this.reversedRegistry.get(id);
	}

	@Override
	public int getTick() {
		return this.tick;
	}

	public void onClose() {
		ObjectIterator<Drawable> iterator = this.componentRegistry.keySet().iterator();
		while (iterator.hasNext()) {
			Drawable drawable = iterator.next();
			drawable.remove(this);
			((DrawableInternal) drawable).rootsInternal.remove(this);
			iterator.remove();
		}
	}

	/**
	 * @deprecated internal
	 */
	@Deprecated
	public void write(Output output) {
		output.writeInt(this.componentRegistry.size());
		for (Drawable drawable : this.componentRegistry.keySet()) {
			drawable.write(this, output);
		}
		output.writeInt(this.panel.getSyncId());
	}

	void tickComponents() {
		this.tickables.forEach(tickable -> tickable.tick(this));
	}
}
