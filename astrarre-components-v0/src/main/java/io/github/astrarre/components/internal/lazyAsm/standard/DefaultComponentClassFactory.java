package io.github.astrarre.components.internal.lazyAsm.standard;

import java.lang.reflect.Constructor;

import io.github.astrarre.components.internal.lazyAsm.DataObjectHolderComponentManager;
import io.github.astrarre.components.internal.util.PublicLoader;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

@SuppressWarnings ("unused")
public class DefaultComponentClassFactory implements Opcodes {
	protected final String factoryDesc, factoryName;
	protected final Class<?> factoryType;

	public DefaultComponentClassFactory() {
		Class<?> factoryType = DataObjectHolderComponentManager.class;
		this.factoryDesc = Type.getDescriptor(factoryType);
		this.factoryName = Type.getInternalName(factoryType);
		this.factoryType = factoryType;
	}

	public <V, T extends Component<?, V>> T createComponent(DataObjectHolderComponentManager<?> manager,
			String modid,
			String value,
			PublicLoader access,
			Class<T> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version) {

		String internalName = this.internalName(manager, modid, value, access, componentType, dataHolderInternalName, fieldName, version);
		String superName = this.superName(manager, modid, value, access, componentType, dataHolderInternalName, fieldName, version);
		String[] interfaces = this.interfaces(manager, modid, value, access, componentType, dataHolderInternalName, fieldName, version);
		ClassWriter visitor = this.createHeader(manager,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				version,
				internalName,
				superName,
				interfaces);
		this.createConstructor(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				version,
				internalName,
				superName,
				interfaces);
		this.createGetterMethod(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superName,
				interfaces);
		this.createSetterMethod(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superName,
				interfaces);

		this.createModidMethod(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superName,
				interfaces);

		this.createIdMethod(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superName,
				interfaces);

		this.createGetComponentManagerMethod(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superName,
				interfaces);

		this.createOnChangeMethod(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				version,
				internalName,
				superName,
				interfaces);

		this.postMethodVisit(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superName,
				interfaces);



		return this.createComponent(manager,
				visitor,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superName,
				interfaces);
	}

	protected void postMethodVisit(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {

	}

	protected String internalName(DataObjectHolderComponentManager<?> manager,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			int version) {
		return "astrarre-components-v0/generated/component/" + modid + "/" + value;
	}

	protected String superName(DataObjectHolderComponentManager<?> manager,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			int version) {
		return "java/lang/Object";
	}

	protected String[] interfaces(DataObjectHolderComponentManager<?> manager,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			int version) {
		return new String[] {Type.getInternalName(componentType)};
	}

	protected ClassWriter createHeader(DataObjectHolderComponentManager<?> manager,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		ClassWriter visitor = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		visitor.visit(V1_8, ACC_PUBLIC, internalName, null, superClass, interfaces);
		return visitor;
	}

	protected void createConstructor(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		FieldVisitor factory = visitor.visitField(ACC_PUBLIC | ACC_FINAL, "factory", this.factoryDesc, null, null);
		FieldVisitor listeners = visitor.visitField(ACC_PUBLIC | ACC_FINAL, "listeners", "Ljava/util/List;", null, null);

		MethodVisitor constructor = visitor.visitMethod(ACC_PUBLIC, "<init>", "(L" + this.factoryName + ";)V", null, null);
		constructor.visitParameter("factory", 0);

		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitMethodInsn(INVOKESPECIAL, superClass, "<init>", "()V", false);

		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitVarInsn(ALOAD, 1);
		constructor.visitFieldInsn(PUTFIELD, internalName, "factory", this.factoryDesc);

		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitTypeInsn(NEW, "java/util/ArrayList");
		constructor.visitInsn(DUP);
		constructor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
		constructor.visitFieldInsn(PUTFIELD, internalName, "listeners", "Ljava/util/List;");

		constructor.visitInsn(RETURN);
		constructor.visitMaxs(2, 3);

	}

	protected void createGetterMethod(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		String simple = componentType.getSimpleName().replace("Component", "");
		MethodVisitor get = visitor.visitMethod(ACC_PUBLIC, "get" + simple, "(Ljava/lang/Object;)" + fieldDesc, null, null);
		get.visitVarInsn(ALOAD, 0);
		get.visitFieldInsn(GETFIELD, internalName, "factory", this.factoryDesc);
		get.visitVarInsn(ALOAD, 1);
		InstructionAdapter adapter = new InstructionAdapter(get);
		adapter.iconst(version);
		get.visitMethodInsn(INVOKEVIRTUAL, this.factoryName, "getDataHolder", "(Ljava/lang/Object;I)Ljava/lang/Object;", false);
		get.visitTypeInsn(CHECKCAST, dataHolderInternalName);
		get.visitFieldInsn(GETFIELD, dataHolderInternalName, fieldName, fieldDesc);
		get.visitInsn(Type.getType(fieldDesc).getOpcode(IRETURN));
		get.visitMaxs(2, 2);
	}

	protected void createSetterMethod(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		String simple = componentType.getSimpleName().replace("Component", "");
		MethodVisitor set = visitor.visitMethod(ACC_PUBLIC, "set" + simple, "(Ljava/lang/Object;" + fieldDesc + ")V", null, null);
		int loadOpcode = Type.getType(fieldDesc).getOpcode(ILOAD);

		set.visitVarInsn(ALOAD, 0); // this
		set.visitFieldInsn(GETFIELD, internalName, "factory", this.factoryDesc); // factory
		set.visitVarInsn(ALOAD, 1); // factory context
		InstructionAdapter adapter2 = new InstructionAdapter(set);
		adapter2.iconst(version); // factory context version
		set.visitMethodInsn(INVOKEVIRTUAL, this.factoryName, "getDataHolder", "(Ljava/lang/Object;I)Ljava/lang/Object;", false);
		set.visitTypeInsn(CHECKCAST, dataHolderInternalName);
		set.visitVarInsn(loadOpcode, 2);
		set.visitFieldInsn(PUTFIELD, dataHolderInternalName, fieldName, fieldDesc);
		this.postSetterMethod(manager,
				visitor,
				set,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superClass,
				interfaces);

		set.visitVarInsn(ALOAD, 0); // this
		set.visitFieldInsn(GETFIELD, internalName, "factory", this.factoryDesc); // factory
		set.visitVarInsn(ALOAD, 0); // factory this
		set.visitFieldInsn(GETFIELD, internalName, "listeners", "Ljava/util/List;"); // factory listeners
		set.visitVarInsn(ALOAD, 1); // factory listeners context
		set.visitVarInsn(loadOpcode, 2); // factory isteners context varprimitive
		set.visitMethodInsn(INVOKEVIRTUAL, this.factoryName, "onChange", "(Ljava/util/List;Ljava/lang/Object;"+fieldDesc+")V");
		set.visitInsn(RETURN);
		set.visitMaxs(2, 3);
	}

	protected void postSetterMethod(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			MethodVisitor method,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {

	}

	protected void createModidMethod(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		MethodVisitor modMethod = visitor.visitMethod(ACC_PUBLIC, "getMod", "()Ljava/lang/String;", null, null);
		modMethod.visitLdcInsn(modid);
		modMethod.visitInsn(ARETURN);
		modMethod.visitMaxs(1, 1);
	}

	protected void createIdMethod(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		MethodVisitor id = visitor.visitMethod(ACC_PUBLIC, "getId", "()Ljava/lang/String;", null, null);
		id.visitLdcInsn(value);
		id.visitInsn(ARETURN);
		id.visitMaxs(1, 1);
	}

	private static final String COMPONENT_MANAGER = Type.getDescriptor(ComponentManager.class);
	protected void createGetComponentManagerMethod(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		MethodVisitor compManagerMethod = visitor.visitMethod(ACC_PUBLIC, "getComponentManager", "()" + COMPONENT_MANAGER, null, null);
		compManagerMethod.visitVarInsn(ALOAD, 0);
		compManagerMethod.visitFieldInsn(GETFIELD, internalName, "factory", this.factoryDesc);
		compManagerMethod.visitInsn(ARETURN);
		compManagerMethod.visitMaxs(2, 1);
	}

	protected void createOnChangeMethod(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<?> componentType,
			String dataHolderInternalName,
			String fieldName,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		MethodVisitor change = visitor.visitMethod(ACC_PUBLIC, "postChange", "(Ljava/util/function/BiConsumer;)V", null, null);
		change.visitVarInsn(ALOAD, 0);
		change.visitFieldInsn(GETFIELD, internalName, "listeners", "Ljava/util/List;");
		change.visitVarInsn(ALOAD, 1);
		change.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		change.visitInsn(RETURN);
		change.visitMaxs(2, 2);
	}

	protected <V, T extends Component<?, V>> T createComponent(DataObjectHolderComponentManager<?> manager,
			ClassWriter visitor,
			String modid,
			String value,
			PublicLoader access,
			Class<T> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		byte[] code = visitor.toByteArray();
		Class<? extends T> component = (Class<? extends T>) access.defineCls(internalName.replace('/', '.'), code, 0, code.length, null);
		return this.instantiate(manager, 
				component,
				modid,
				value,
				access,
				componentType,
				dataHolderInternalName,
				fieldName,
				fieldDesc,
				version,
				internalName,
				superClass,
				interfaces);
	}

	protected <V, T extends Component<?, V>> T instantiate(DataObjectHolderComponentManager<?> manager,
			Class<? extends T> componentClass,
			String modid,
			String value,
			PublicLoader access,
			Class<T> componentType,
			String dataHolderInternalName,
			String fieldName,
			String fieldDesc,
			int version,
			String internalName,
			String superClass,
			String[] interfaces) {
		try {
			// io.github.astrarre.components.internal.lazyAsm.DataObjectHolderComponentFactory
			// io.github.astrarre.components.internal.lazyAsm.DataObjectHolderComponentFactory
			Constructor<T> constructor = (Constructor<T>) componentClass.getConstructors()[0];
			return (T) constructor.newInstance(manager);
		} catch (ReflectiveOperationException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
}
