package io.github.astrarre.access.internal.factory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import io.github.astrarre.util.internal.AsmHelper;
import io.github.astrarre.util.internal.factory.FieldPrototype;
import io.github.astrarre.util.internal.factory.ProxyMethodBuilder;
import io.github.astrarre.util.v0.api.Validate;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public class ContextExtractingMethodBuilder implements ProxyMethodBuilder {
	final String functionName, nullFunctionName;
	final Method method;
	final Class<?> paramType;
	final int ordinal;

	public ContextExtractingMethodBuilder(String name, String nullFunctionName, Method method, Class<?> type, int ordinal) {
		this.functionName = name;
		this.nullFunctionName = nullFunctionName;
		this.method = method;
		this.paramType = type;
		this.ordinal = ordinal;
	}

	@Override
	public void emit(ClassNode node) {
		String desc = Type.getMethodDescriptor(this.method);
		MethodVisitor visitor = node.visitMethod(ACC_PUBLIC | ACC_FINAL, this.method.getName(), desc, null, null);
		int index = this.getParamIndex();
		Validate.greaterThanEqualTo(0, index, "no parameter with type " + this.paramType + " and ordinal " + this.ordinal + " found!");

		// get function getter
		visitor.visitVarInsn(ALOAD, 0);
		visitor.visitFieldInsn(GETFIELD, node.name, this.functionName, "Ljava/util/Function;");

		Type methodType = Type.getMethodType(desc);
		Type[] args = methodType.getArgumentTypes();
		int localIndex = this.getLocalIndex(index, args);
		Type type = args[index];

		// get function with context
		visitor.visitVarInsn(type.getOpcode(ILOAD), localIndex);
		AsmHelper.emitWrapInstruction(visitor, type.getSort());

		Class<?> declaringType = this.method.getDeclaringClass();
		String className = Type.getInternalName(declaringType);
		visitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		visitor.visitTypeInsn(CHECKCAST, className);
		visitor.visitInsn(DUP);
		visitor.visitVarInsn(ASTORE, 1 + Arrays.stream(args).mapToInt(Type::getSize).sum());
		Label label = new Label();
		visitor.visitInsn(ACONST_NULL);
		visitor.visitJumpInsn(IF_ACMPNE, label); // if value == null {value = this.nullFunction}
		visitor.visitFieldInsn(GETFIELD, node.name, this.nullFunctionName, "L"+className+";");

		visitor.visitLabel(label);
		int localVar = 1;
		for(Type arg : args) {
			visitor.visitVarInsn(type.getOpcode(ILOAD), localVar);
			localVar += arg.getSize();
		}


		visitor.visitMethodInsn(INVOKEVIRTUAL, className, this.method.getName(), desc, declaringType.isInterface()); // invoke method
		visitor.visitInsn(methodType.getReturnType().getOpcode(IRETURN));
	}

	private int getLocalIndex(int index, Type[] args) {
		int localIndex = 0;
		for(int i = 0; i < index; i++) {
			localIndex += args[i].getSize();
		}
		return localIndex;
	}

	private int getParamIndex() {
		int ord = this.ordinal;
		Class<?>[] types = this.method.getParameterTypes();
		int index = -1;
		for(int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			if(type == this.paramType && ord-- <= 0) {
				index = i;
				break;
			}
		}
		return index;
	}

	@Override
	public void requestFields(Set<FieldPrototype> fields) {
		fields.add(new FieldPrototype(this.functionName, "Ljava/util/Function;"));
		fields.add(new FieldPrototype(this.nullFunctionName, Type.getDescriptor(this.method.getDeclaringClass())));
	}
}
