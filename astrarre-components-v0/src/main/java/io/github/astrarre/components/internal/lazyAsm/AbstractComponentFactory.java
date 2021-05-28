package io.github.astrarre.components.internal.lazyAsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.astrarre.components.internal.access.CopyAccess;
import io.github.astrarre.components.v0.api.components.BoolComponent;
import io.github.astrarre.components.v0.api.components.ByteComponent;
import io.github.astrarre.components.v0.api.components.CharComponent;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.components.DoubleComponent;
import io.github.astrarre.components.v0.api.components.FloatComponent;
import io.github.astrarre.components.v0.api.components.IntComponent;
import io.github.astrarre.components.v0.api.components.LongComponent;
import io.github.astrarre.components.v0.api.components.ShortComponent;
import io.github.astrarre.components.v0.api.factory.ComponentFactory;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public abstract class AbstractComponentFactory<C> extends ComponentFactory<C> implements Opcodes {
	private static final Map<Class<?>, Class<?>> TO_JAVA_MAP = new HashMap<>();
	static {
		TO_JAVA_MAP.put(BoolComponent.class, boolean.class);
		TO_JAVA_MAP.put(ByteComponent.class, boolean.class);
		TO_JAVA_MAP.put(CharComponent.class, boolean.class);
		TO_JAVA_MAP.put(Component.class, boolean.class);
		TO_JAVA_MAP.put(DoubleComponent.class, boolean.class);
		TO_JAVA_MAP.put(FloatComponent.class, boolean.class);
		TO_JAVA_MAP.put(IntComponent.class, boolean.class);
		TO_JAVA_MAP.put(LongComponent.class, boolean.class);
		TO_JAVA_MAP.put(ShortComponent.class, boolean.class);
	}

	public static final Loader LOADER = new Loader();
	private static final String COPY_ACCESS = Type.getInternalName(CopyAccess.class);
	private static final String FACTORY = Type.getInternalName(AbstractComponentFactory.class);
	private static final String FACTORY_DESC = Type.getDescriptor(AbstractComponentFactory.class);
	protected final String name;
	protected DataHolderClass activeClass;

	public AbstractComponentFactory(String modid, String path) {
		this.name = modid + "__" + path;
	}

	@Override
	public <V, T extends Component<C, V>> T create(Class<T> componentType, String modid, String path) {
		String id = modid + "__" + path;
		if(this.activeClass == null || this.activeClass.compiled != null) {
			int version = this.activeClass == null ? 1 : (this.activeClass.version + 1);
			this.activeClass = new DataHolderClass(this.activeClass, version, "astrarre-components-v0/generated/dataholder" + this.name + version);
		}

		FieldPrototype prototype = new FieldPrototype(Type.getDescriptor(TO_JAVA_MAP.get(componentType)), id, null);
		this.activeClass.fields.add(prototype);
		try {
			Class<?> c = this.generateComponentClass(componentType, this.activeClass.name, prototype, id);
			return (T) c.getConstructor(AbstractComponentFactory.class, int.class).newInstance(this, this.activeClass.version);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	public Object getDataHolder(C context, int componentVersion) {
		int version = this.getVersion(context);
		CopyAccess oldData = this.getData(context);
		if(componentVersion > version) {
			if(this.activeClass.compiled == null) {
				this.activeClass.compiled = this.generateDataHolderClass(this.activeClass);
			}
			CopyAccess newData = this.createNewDataHolder();
			if(oldData != null) {
				oldData.copyTo(newData);
			}
			this.setData(context, newData, this.activeClass.version);
			return newData;
		} else {
			return oldData;
		}
	}

	protected CopyAccess createNewDataHolder() {
		try {
			return (CopyAccess) this.activeClass.compiled.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	protected Class<?> generateComponentClass(Class<?> componentType, String dataHolderName, FieldPrototype prototype, String id) {
		ClassWriter visitor = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		String superClass = this.componentClassSuper();
		String internalName = "astrarre-components-v0/generated/component/" + id.replace(':', '/');
		visitor.visit(V1_8,
				ACC_PUBLIC,
				internalName,
				null,
				superClass,
				this.componentClassInterface(componentType, prototype));
		FieldVisitor factory = visitor.visitField(ACC_PUBLIC | ACC_FINAL, "factory", FACTORY_DESC, null, null);
		FieldVisitor version = visitor.visitField(ACC_PUBLIC | ACC_FINAL, "version", "I", null, null);

		MethodVisitor constructor = visitor.visitMethod(ACC_PUBLIC, "<init>", "(L"+FACTORY+";I)V", null, null);
		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitMethodInsn(INVOKESPECIAL, superClass, "<init>", "()V", false);

		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitVarInsn(ALOAD, 1);
		constructor.visitFieldInsn(PUTFIELD, internalName, "factory", FACTORY_DESC);

		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitVarInsn(ILOAD, 2);
		constructor.visitFieldInsn(PUTFIELD, internalName, "version", "I");

		constructor.visitInsn(RETURN);
		constructor.visitMaxs(2, 3);

		String simple = componentType.getSimpleName().replace("Component", "");
		MethodVisitor get = visitor.visitMethod(ACC_PUBLIC, "get" + simple, "(Ljava/lang/Object;)" + prototype.type, null, null);
		get.visitVarInsn(ALOAD, 0);
		get.visitFieldInsn(GETFIELD, internalName, "factory", FACTORY_DESC);
		get.visitVarInsn(ALOAD, 1);
		get.visitVarInsn(ALOAD, 0);
		get.visitFieldInsn(GETFIELD, internalName, "version", "I");
		get.visitMethodInsn(INVOKEVIRTUAL, FACTORY, "getDataHolder", "(Ljava/lang/Object;I)Ljava/lang/Object;", false);
		get.visitTypeInsn(CHECKCAST, dataHolderName);
		get.visitFieldInsn(GETFIELD, dataHolderName, prototype.name, prototype.type);
		get.visitInsn(Type.getType(prototype.type).getOpcode(IRETURN));
		get.visitMaxs(2, 2);

		MethodVisitor set = visitor.visitMethod(ACC_PUBLIC, "set" + simple, "(Ljava/lang/Object;"+prototype.type+")V", null, null);
		set.visitVarInsn(ALOAD, 0);
		set.visitFieldInsn(GETFIELD, internalName, "factory", FACTORY_DESC);
		set.visitVarInsn(ALOAD, 1);
		set.visitVarInsn(ALOAD, 0);
		set.visitFieldInsn(GETFIELD, internalName, "version", "I");
		set.visitMethodInsn(INVOKEVIRTUAL, FACTORY, "getDataHolder", "(Ljava/lang/Object;I)Ljava/lang/Object;", false);
		set.visitTypeInsn(CHECKCAST, dataHolderName);
		set.visitVarInsn(Type.getType(prototype.type).getOpcode(ILOAD), 2);
		set.visitFieldInsn(PUTFIELD, dataHolderName, prototype.name, prototype.type);
		set.visitInsn(RETURN);
		set.visitMaxs(2, 3);

		this.postProcessComponentClass(visitor, componentType, dataHolderName, prototype, id);
		byte[] code = visitor.toByteArray();
		return LOADER.define(internalName.replace('/', '.'), code, 0, code.length);
	}

	protected String componentClassSuper() {
		return "java/lang/Object";
	}

	protected String[] componentClassInterface(Class<?> componentType, FieldPrototype prototype) {
		return new String[] {Type.getInternalName(componentType)};
	}

	protected void postProcessComponentClass(ClassWriter writer, Class<?> componentType, String dataHolderName, FieldPrototype prototype, String id) {}

	protected Class<?> generateDataHolderClass(DataHolderClass current) {
		ClassWriter visitor = new ClassWriter(0);
		String parent = current.parent == null ? "java/lang/Object" : current.parent.name;
		visitor.visit(V1_8,
				ACC_PUBLIC,
				current.name,
				null,
				parent,
				this.dataHolderInterfaces());

		MethodVisitor constructor = visitor.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitMethodInsn(INVOKESPECIAL, parent, "<init>", "()V", false);
		constructor.visitInsn(RETURN);
		constructor.visitMaxs(1, 1);

		for (FieldPrototype field : current.fields) {
			FieldVisitor f = visitor.visitField(ACC_PUBLIC, field.name, field.type, null, field.value);
			f.visitEnd();
		}

		MethodVisitor copy = visitor.visitMethod(ACC_PUBLIC, "copyTo", "(Ljava/lang/Object;)V", null, null);
		if (current.parent != null) {
			copy.visitVarInsn(ALOAD, 0);
			copy.visitVarInsn(ALOAD, 1);
			copy.visitMethodInsn(INVOKESPECIAL, parent, "copyTo", "(Ljava/lang/Object;)V", false);
		}
		for (FieldPrototype field : current.fields) {
			copy.visitVarInsn(ALOAD, 1);
			copy.visitTypeInsn(CHECKCAST, current.name);
			copy.visitVarInsn(ALOAD, 0);
			copy.visitFieldInsn(GETFIELD, current.name, field.name, field.type);
			copy.visitFieldInsn(PUTFIELD, current.name, field.name, field.type);
		}
		copy.visitInsn(RETURN);
		copy.visitMaxs(2, 2);
		this.postProcessDataHolderClass(visitor, current);
		byte[] code = visitor.toByteArray();
		return LOADER.define(current.name.replace('/', '.'), code, 0, code.length);
	}

	protected String[] dataHolderInterfaces() {
		return new String[] {COPY_ACCESS};
	}

	protected void postProcessDataHolderClass(ClassWriter writer, DataHolderClass cls) {
	}

	public static final class Loader extends ClassLoader {
		public Class<?> define(String name, byte[] code, int off, int len) {
			return this.defineClass(name, code, off, len);
		}
	}

	public static class DataHolderClass {
		@Nullable public final DataHolderClass parent;
		public final int version;
		public final List<FieldPrototype> fields = new ArrayList<>();
		public final String name;
		public Class<?> compiled;

		public DataHolderClass(@Nullable DataHolderClass parent, int version, String name) {
			this.parent = parent;
			this.version = version;
			this.name = name;
		}
	}
}
