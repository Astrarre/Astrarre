package io.github.astrarre.components.internal.lazyAsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import io.github.astrarre.components.v0.api.factory.DataObjectHolder;
import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;
import io.github.astrarre.components.internal.lazyAsm.standard.DefaultComponentClassFactory;
import io.github.astrarre.components.internal.lazyAsm.standard.DefaultDataHolderClassFactory;
import io.github.astrarre.components.v0.api.components.BoolComponent;
import io.github.astrarre.components.v0.api.components.ByteComponent;
import io.github.astrarre.components.v0.api.components.CharComponent;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.components.DoubleComponent;
import io.github.astrarre.components.v0.api.components.FloatComponent;
import io.github.astrarre.components.v0.api.components.IntComponent;
import io.github.astrarre.components.v0.api.components.LongComponent;
import io.github.astrarre.components.v0.api.components.ShortComponent;
import io.github.astrarre.components.internal.util.FieldPrototype;
import io.github.astrarre.components.internal.util.PublicLoader;
import org.objectweb.asm.Type;

public class DataObjectHolderComponentManager<C>
		implements org.objectweb.asm.Opcodes, io.github.astrarre.components.v0.api.factory.ComponentManager<C> {
	public static final Map<Class<?>, Class<?>> COMPONENT_TYPE_MAP;

	static {
		Map<Class<?>, Class<?>> map = new HashMap<>();
		map.put(BoolComponent.class, boolean.class);
		map.put(ByteComponent.class, byte.class);
		map.put(CharComponent.class, char.class);
		map.put(Component.class, Object.class);
		map.put(DoubleComponent.class, double.class);
		map.put(FloatComponent.class, float.class);
		map.put(IntComponent.class, int.class);
		map.put(LongComponent.class, long.class);
		map.put(ShortComponent.class, short.class);
		COMPONENT_TYPE_MAP = Collections.unmodifiableMap(map);
	}

	protected final List<Component<C, ?>> components = new ArrayList<>();
	protected final String name;
	protected DataHolderClass activeClass;
	protected final PublicLoader classLoader = new PublicLoader(this.getClass().getClassLoader());
	protected final DefaultDataHolderClassFactory dataFactory = new DefaultDataHolderClassFactory(this.classLoader);
	protected final DefaultComponentClassFactory componentFactory = new DefaultComponentClassFactory();

	public DataObjectHolderComponentManager(
			String modid,
			String path) {
		this.name = modid + "__" + path;
	}

	protected int getVersion(C context) {
		return ((DataObjectHolder) context).astrarre_getVersion();
	}

	protected Object getData(C context) {
		return ((DataObjectHolder) context).astrarre_getObject();
	}

	protected void copyTo(Object from, Object to) {
		((CopyAccess) from).copyTo(to);
	}

	protected void setData(C context, Object data, int version) {
		((DataObjectHolder) context).astrarre_setObject((CopyAccess) data, version);
	}

	@Override
	public <V, T extends Component<C, V>> T create(Class<T> componentType, String modid, String path) {
		if (this.activeClass == null || this.activeClass.compiled != null) {
			int version = this.activeClass == null ? 1 : (this.activeClass.version + 1);
			this.activeClass = new DataHolderClass(this.activeClass, version, "astrarre-components-v0/generated/dataholder" + this.name + version);
		}

		String fieldName = modid + "__" + path;
		FieldPrototype prototype = new FieldPrototype(Type.getDescriptor(COMPONENT_TYPE_MAP.get(componentType)), fieldName, null);
		this.activeClass.fields.add(prototype);
		int version = this.activeClass.version;
		return this.componentFactory.createComponent(this,
				modid,
				path, this.classLoader,
				componentType,
				this.activeClass.name,
				prototype.name,
				prototype.type,
				version);
	}

	/**
	 * called from asm code
	 */
	@Deprecated
	public Object getDataHolder(C context, int componentVersion) {
		int version = this.getVersion(context);
		Object oldData = this.getData(context);
		if (componentVersion > version) {
			if (this.activeClass.compiled == null) {
				this.activeClass.compiled = this.dataFactory.createDataClassCreator(this, this.activeClass);
			}
			Object newData = this.createNewDataHolder();
			if (oldData != null) {
				this.copyTo(oldData, newData);
			}
			this.setData(context, newData, this.activeClass.version);
			return newData;
		} else {
			return oldData;
		}
	}

	@Deprecated
	public void onChange(List<BiConsumer<C, Boolean>> consumers, C context, boolean old) {
		if(consumers.isEmpty()) return;
		this.onChange(consumers, context, (Boolean) old);
	}

	@Deprecated
	public void onChange(List<BiConsumer<C, Byte>> consumers, C context, byte old) {
		if(consumers.isEmpty()) return;
		this.onChange(consumers, context, (Byte) old);
	}

	@Deprecated
	public void onChange(List<BiConsumer<C, Character>> consumers, C context, char old) {
		if(consumers.isEmpty()) return;
		this.onChange(consumers, context, (Character) old);
	}

	@Deprecated
	public void onChange(List<BiConsumer<C, Short>> consumers, C context, short old) {
		if(consumers.isEmpty()) return;
		this.onChange(consumers, context, (Short) old);
	}

	@Deprecated
	public void onChange(List<BiConsumer<C, Integer>> consumers, C context, int old) {
		if(consumers.isEmpty()) return;
		this.onChange(consumers, context, (Integer) old);
	}

	@Deprecated
	public void onChange(List<BiConsumer<C, Float>> consumers, C context, float old) {
		if(consumers.isEmpty()) return;
		this.onChange(consumers, context, (Float) old);
	}

	@Deprecated
	public void onChange(List<BiConsumer<C, Long>> consumers, C context, long old) {
		if(consumers.isEmpty()) return;
		this.onChange(consumers, context, (Long) old);
	}

	@Deprecated
	public void onChange(List<BiConsumer<C, Double>> consumers, C context, double old) {
		if(consumers.isEmpty()) return;
		this.onChange(consumers, context, (Double) old);
	}

	// too lazy to do for loop in bytecode
	@Deprecated
	public <V> void onChange(List<BiConsumer<C, V>> consumers, C context, V old) {
		for (BiConsumer<C, V> consumer : consumers) {
			consumer.accept(context, old);
		}
	}

	protected Object createNewDataHolder() {
		return this.activeClass.compiled.get();
	}

	@Override
	public Iterable<Component<C, ?>> getComponents() {
		return this.components;
	}
}
