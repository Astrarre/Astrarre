package io.github.astrarre.components.internal.lazyAsm.standard;

import java.util.function.Supplier;

import io.github.astrarre.components.internal.lazyAsm.DataHolderClass;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.internal.util.FieldPrototype;
import io.github.astrarre.components.internal.util.PublicLoader;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class DefaultDataHolderClassFactory implements Opcodes {
	public static final DefaultDataHolderClassFactory INSTANCE = new DefaultDataHolderClassFactory();

	protected DefaultDataHolderClassFactory() {
	}

	public Supplier<Object> createDataClassCreator(ComponentManager<?> manager, DataHolderClass current) {
		ClassWriter visitor = new ClassWriter(0);
		String parent = current.parent == null ? "java/lang/Object" : current.parent.name;
		visitor.visit(V1_8, ACC_PUBLIC, current.name, null, parent, this.interfaces(manager, current));

		this.createConstructor(manager, visitor, parent);
		this.createFields(manager, current, visitor);
		this.createCopyMethod(current, visitor, parent);

		byte[] code = visitor.toByteArray();
		Class<?> cls = PublicLoader.INSTANCE.defineCls(current.name.replace('/', '.'), code, 0, code.length, null);
		return this.create(manager, current, cls);
	}

	@NotNull
	private Supplier<Object> create(ComponentManager<?> manager, DataHolderClass current, Class<?> cls) {
		return () -> {
			try {
				return (Object) cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		};
	}


	private void createCopyMethod(DataHolderClass current, ClassWriter visitor, String parent) {
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
	}

	private void createFields(ComponentManager<?> manager, DataHolderClass current, ClassWriter visitor) {
		for (FieldPrototype field : current.fields) {
			FieldVisitor f = visitor.visitField(ACC_PUBLIC, field.name, field.type, null, field.value);
			f.visitEnd();
		}
	}

	private void createConstructor(ComponentManager<?> manager, ClassWriter visitor, String parent) {
		MethodVisitor constructor = visitor.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitMethodInsn(INVOKESPECIAL, parent, "<init>", "()V", false);
		constructor.visitInsn(RETURN);
		constructor.visitMaxs(1, 1);
	}

	protected String[] interfaces(ComponentManager<?> manager, DataHolderClass current) {
		return new String[]{Type.getInternalName(CopyAccess.class)};
	}
}
