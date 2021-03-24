package io.github.astrarre.gui.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;

public abstract class RootContainerInternal implements RootContainer {
	static final AtomicInteger ID = new AtomicInteger(), CLIENT_ID = new AtomicInteger(Integer.MIN_VALUE);
	protected final List<Tickable> tickables = new ArrayList<>();
	protected final APanel panel;
	private final Object2IntOpenHashMap<ADrawable> componentRegistry = new Object2IntOpenHashMap<>();
	private final Int2ObjectOpenHashMap<ADrawable> reversedRegistry = new Int2ObjectOpenHashMap<>();
	int tick;
	private boolean reading;
	private final Serializer<ADrawable> serializer = new DrawableSerializer(this);

	protected RootContainerInternal() {
		this.addRoot(this.panel = new APanel());
	}

	protected RootContainerInternal(PacketByteBuf input) {
		this(internal -> {}, input);
	}

	protected RootContainerInternal(Consumer<RootContainerInternal> toRun, PacketByteBuf input) {
		this.reading = true;
		toRun.accept(this);
		int size = input.readInt();
		for (int i = 0; i < size; i++) {
			ADrawable drawable = this.getSerializer().read(FabricViews.view(input.readCompoundTag()), "drawable");
			this.addSynced(drawable);
		}
		int panelId = input.readInt();
		this.reading = false;
		this.panel = (APanel) this.forId(panelId);
		for (ADrawable value : this.reversedRegistry.values()) {
			((DrawableInternal) value).onAdded(this);
		}
	}

	/**
	 * @deprecated internal
	 */
	@Deprecated
	public void write(PacketByteBuf output) {
		output.writeInt(this.componentRegistry.size());
		for (ADrawable drawable : this.componentRegistry.keySet()) {
			NBTagView.Builder builder = NBTagView.builder();
			this.getSerializer().save(builder, "drawable", drawable);
			output.writeCompoundTag(builder.toTag());
		}
		output.writeInt(this.panel.getSyncId());
	}

	void addSynced(ADrawable drawable) {
		if (drawable instanceof Tickable) {
			this.tickables.add((Tickable) drawable);
		}

		this.componentRegistry.put(drawable, drawable.getSyncId());
		this.reversedRegistry.put(drawable.getSyncId(), drawable);
		((DrawableInternal) drawable).rootsInternal.add(this);
		((DrawableInternal) drawable).isClient = this.isClient();
	}

	@Override
	public APanel getContentPanel() {
		return this.panel;
	}

	@Override
	public void addRoot(ADrawable drawable) {
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
		((DrawableInternal) drawable).isClient = this.isClient();
		((DrawableInternal) drawable).onAdded(this);

		if (!this.isClient()) {
			NetworkMember member = this.getViewer();
			if (member != null) {
				GuiPacketHandler.addDrawable(this, member, drawable);
			}
		}
	}

	@Override
	public void removeRoot(ADrawable drawable) {
		if(drawable == null) return;

		int id = drawable.getSyncId();
		this.reversedRegistry.remove(id);
		this.componentRegistry.removeInt(drawable);
		if(drawable instanceof Tickable) {
			this.tickables.remove(drawable);
		}
		drawable.remove(this);
		((DrawableInternal) drawable).rootsInternal.remove(this);
		if (!this.isClient()) {
			NetworkMember member = this.getViewer();
			if (member != null) {
				GuiPacketHandler.removeDrawable(member, this, id);
			}
		}
	}

	@Override
	public <T extends ADrawable & Interactable> void setFocus(T drawable) {
		this.panel.setFocused(this, drawable, -1);
	}

	@Override
	@Nullable
	public ADrawable forId(int id) {
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
		ObjectIterator<ADrawable> iterator = this.componentRegistry.keySet().iterator();
		while (iterator.hasNext()) {
			ADrawable drawable = iterator.next();
			drawable.remove(this);
			((DrawableInternal) drawable).rootsInternal.remove(this);
			iterator.remove();
		}
	}

	void tickComponents() {
		this.tickables.forEach(tickable -> tickable.tick(this));
	}

	@Override
	public Serializer<ADrawable> getSerializer() {
		return this.serializer;
	}
}
