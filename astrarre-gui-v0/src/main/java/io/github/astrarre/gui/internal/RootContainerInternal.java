package io.github.astrarre.gui.internal;

import java.util.Random;
import java.util.function.Consumer;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
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
	private static final Random RANDOM = new Random();
	private final Object2IntOpenHashMap<Drawable> componentRegistry = new Object2IntOpenHashMap<>();
	private final Int2ObjectOpenHashMap<Drawable> reversedRegistry = new Int2ObjectOpenHashMap<>();
	protected final Panel panel;
	private boolean reading;
	int tick;
	private int nextId = RANDOM.nextInt(), nextClientId = this.nextId + Integer.MAX_VALUE;

	protected RootContainerInternal() {
		this.panel = new Panel(this);
	}

	protected RootContainerInternal(Consumer<RootContainerInternal> toRun, Input input) {
		this.reading = true;
		toRun.accept(this);
		int size = input.readInt();
		for (int i = 0; i < size && input.bytes() > 0; i++) {
			Drawable drawable = Drawable.read(this, input);
			this.addSynced(drawable);
		}

		this.reading = false;
		int panelId = input.readInt();
		this.panel = (Panel) this.forId(panelId);
	}

	protected RootContainerInternal(Input input) {
		this(internal -> {}, input);
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

	@Override
	public <T extends Drawable & Interactable> void setFocus(T drawable) {
		this.panel.setFocused(drawable, -1);
	}

	@Override
	@Nullable
	public Drawable forId(int id) {
		if(this.reading) {
			throw new IllegalStateException("cannot check root container for id while it is being read (store the ids inside some internal buffer and query when you need to)");
		}
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
	@Deprecated
	public void write(Output output) {
		output.writeInt(this.componentRegistry.size());
		for (Drawable drawable : this.componentRegistry.keySet()) {
			drawable.write(output);
		}
		output.writeInt(this.panel.getSyncId());
	}

	void addSynced(Drawable drawable) {
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
	}

	@Override
	public int tick() {
		return this.tick;
	}
}
