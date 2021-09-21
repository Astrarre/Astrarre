package io.github.astrarre.util.internal.factory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.github.astrarre.util.v0.api.Validate;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

@SuppressWarnings("unchecked")
public class ProxyClassFactory<T> implements Opcodes { // todo allow setting superclass, requires constructor support
	private static final ClsLdr LDR = new ClsLdr();
	public final Class<T> functionClass;
	final String internalName;
	final List<ProxyMethodBuilder> factories = new ArrayList<>();
	Class<? extends T> defined;
	Constructor<? extends T> constructor;
	List<FieldPrototype> fields;

	public ProxyClassFactory(Class<T> aClass) {
		this.functionClass = aClass;
		this.internalName = Type.getInternalName(aClass);
	}

	public T init(Map<String, ?> values) {
		if(this.defined == null) {
			this.emit();
		}
		var vals = new Object[this.fields.size()];
		for(int i = 0; i < this.fields.size(); i++) {
			vals[i] = values.get(this.fields.get(i).name());
		}
		try {
			return this.constructor.newInstance(vals);
		} catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw Validate.rethrow(e);
		}
	}

	public static Class<?> define(ClassNode node) {
		return LDR.def(node);
	}

	public void add(ProxyMethodBuilder factory) {
		this.factories.add(factory);
	}

	public void emit() {
		ClassNode node = new ClassNode();
		String className = UUID.randomUUID().toString().replace('-', '/');
		node.visit(V1_8, ACC_PUBLIC | ACC_FINAL, className, null, "java/lang/Object", new String[] {this.internalName});
		Set<FieldPrototype> prototypes = new HashSet<>();
		for(ProxyMethodBuilder factory : this.factories) {
			factory.requestFields(prototypes);
			factory.emit(node);
		}

		StringBuilder desc = new StringBuilder("(");
		List<FieldPrototype> inOrder = new ArrayList<>();
		MethodNode constructor = new MethodNode();
		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		node.methods.add(constructor);
		constructor.access = ACC_PUBLIC;
		constructor.name = "<init>";

		int index = 1;
		for(FieldPrototype prototype : prototypes) {
			node.visitField(ACC_PRIVATE | ACC_FINAL, prototype.name(), prototype.desc(), null, null);
			inOrder.add(prototype);
			desc.append(prototype.desc());
			constructor.visitVarInsn(ALOAD, 0);
			var type = Type.getType(prototype.desc());
			constructor.visitVarInsn(type.getOpcode(ILOAD), index);
			constructor.visitFieldInsn(PUTFIELD, className, prototype.name(), prototype.desc());
			index += type.getSize();
		}

		desc.append(")V");
		constructor.desc = desc.toString();
		constructor.visitInsn(RETURN);
		this.fields = inOrder;
		this.defined = (Class<? extends T>) define(node);
		this.constructor = (Constructor<? extends T>) this.defined.getConstructors()[0];
	}

	static final class ClsLdr extends ClassLoader {
		public Class<?> def(ClassNode node) {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			node.accept(writer);
			byte[] code = writer.toByteArray();
			return this.defineClass(node.name.replace('/', '.'), code, 0, code.length);
		}
	}
}
