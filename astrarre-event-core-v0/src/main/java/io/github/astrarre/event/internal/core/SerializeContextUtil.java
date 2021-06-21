package io.github.astrarre.event.internal.core;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.event.v0.api.core.ContextView;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class SerializeContextUtil {
	public static List<Context<?>> initContext(List<InternalContexts.SerializeEntry<?>> sers) {
		List<Context<?>> loaded = new ArrayList<>();
		for (InternalContexts.SerializeEntry<?> ser : sers) {
			loaded.add(extract(ser));
		}
		return loaded;
	}

	static <T> Context<T> extract(InternalContexts.SerializeEntry<T> copy) {
		List<T> list = new ArrayList<>();
		for (T o : copy.view()) {
			list.add(o);
		}
		return new Context<>(list, copy);
	}

	public static NbtCompound serializeContext(List<Context<?>> sers) {
		NbtCompound compound = new NbtCompound();
		for (Context<?> ser : sers) {
			compound.put(ser.view.id().toString(), serializeContext(ser));
		}
		return compound;
	}

	static <T> NbtList serializeContext(Context<T> context) {
		NbtList list = new NbtList();
		for (T t : context.list) {
			list.add(NBTagView.builder().putValue("value", context.view.serializer().save(t)).build().toTag());
		}
		return list;
	}

	public static List<Context<?>> deserialize(List<InternalContexts.SerializeEntry<?>> sers, NbtCompound compound) {
		List<Context<?>> contexts = new ArrayList<>();
		for (InternalContexts.SerializeEntry<?> ser : sers) {
			NbtList list = (NbtList) compound.get(ser.id().toString());
			if (list != null) {
				contexts.add(deserialize(ser, list));
			}
		}
		return contexts;
	}

	static <T> Context<T> deserialize(InternalContexts.SerializeEntry<T> ser, NbtList list) {
		List<T> toPop = new ArrayList<>();
		for (NbtElement element : list) {
			T read = ser.serializer().read(((NBTagView) element).getValue("value"));
			toPop.add(read);
		}
		return new Context<>(toPop, ser);
	}

	public static void loadContext(List<Context<?>> contexts) {
		for (Context<?> context : contexts) {
			load(context);
		}
	}

	static <T> void load(Context<T> context) {
		ContextView<T> view = context.view().view();
		for (T t : context.list) {
			InternalContexts.put(view, t);
		}
	}

	public static void popContext(List<Context<?>> contexts) {
		for (Context<?> context : contexts) {
			pop(context);
		}
	}

	static <T> void pop(Context<T> context) {
		ContextView<T> view = context.view().view();
		List<T> list = context.list;
		for (int i = list.size() - 1; i >= 0; i--) {
			T t = list.get(i);
			InternalContexts.pop(view, t);
		}
	}

	record Context<T>(List<T> list, InternalContexts.SerializeEntry<T> view) {}
}
