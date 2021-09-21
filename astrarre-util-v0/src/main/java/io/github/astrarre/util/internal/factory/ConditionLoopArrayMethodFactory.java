package io.github.astrarre.util.internal.factory;

import java.lang.reflect.Method;
import java.util.Set;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ConditionLoopArrayMethodFactory extends RunLoopArrayMethodFactory {
	final String predicateName;
	final String predicateDesc;
	final String nullName;
	final boolean function;

	public ConditionLoopArrayMethodFactory(Method method, String nullFunctionName, String predicateName, boolean function) {
		super(method);
		this.function = function;
		Class<?> ret = method.getReturnType();
		if(ret == void.class) {
			throw new IllegalArgumentException("method cannot have void return type on default!");
		}
		this.nullName = nullFunctionName;
		this.predicateName = predicateName;
		this.predicateDesc = SORT_TO_PREDICATE_TYPE.get(ret);
	}

	@Override
	public void requestFields(Set<FieldPrototype> fields) {
		super.requestFields(fields);
		fields.add(new FieldPrototype(this.nullName, this.function ? "L" + this.internalName + ";" : this.methodType.getReturnType() + ""));
		fields.add(new FieldPrototype(this.predicateName, this.predicateDesc));
	}

	@Override
	public void emitPostInit(MethodVisitor visitor,
			String className,
			String arrayFieldName,
			String arrayFieldDesc,
			int arrayLocalVarIndex,
			int arrayLengthLocalVarIndex) {
		super.emitPostInit(visitor, className, arrayFieldName, arrayFieldDesc, arrayLocalVarIndex, arrayLengthLocalVarIndex);
		int condition = arrayLocalVarIndex + 4;
		visitor.visitVarInsn(ALOAD, 0);
		visitor.visitFieldInsn(GETFIELD, className, this.predicateName, this.predicateDesc);
		visitor.visitVarInsn(ASTORE, condition);
	}

	@Override
	public void emitLoop(MethodVisitor visitor,
			String className,
			String arrayFieldName,
			String arrayFieldDesc,
			int arrayLocalVarIndex,
			int arrayLengthLocalVarIndex,
			int indexLocalVarIndex,
			Label exitLabel,
			Label endOfLoopLabel,
			int functionLocalVarIndex) {
		int condition = arrayLocalVarIndex + 4;
		visitor.visitVarInsn(ALOAD, condition); // function, condition
		super.emitLoop(visitor,
		               className,
		               arrayFieldName,
		               arrayFieldDesc,
		               arrayLocalVarIndex,
		               arrayLengthLocalVarIndex,
		               indexLocalVarIndex,
		               exitLabel,
		               endOfLoopLabel,
		               functionLocalVarIndex);
	}

	@Override
	public void emitExit(MethodVisitor visitor,
			String className,
			String arrayFieldName,
			String arrayFieldDesc,
			int arrayLocalVarIndex,
			int arrayLengthLocalVarIndex,
			int indexLocalVarIndex,
			Label exitLabel,
			Label endOfLoopLabel,
			int valueLocalVarIndex) {
		String desc;
		var type = this.getMethod().getReturnType();
		if(type.isPrimitive()) {
			desc = "(" + Type.getDescriptor(type) + ")Z";
		} else {
			desc = "(Ljava/lang/Object;)Z";
		}
		// return value is on stack
		Type ret = this.methodType.getReturnType();
		int temp = arrayLocalVarIndex + 5;
		visitor.visitVarInsn(ret.getOpcode(ISTORE), temp);
		visitor.visitVarInsn(ret.getOpcode(ILOAD), temp);
		visitor.visitMethodInsn(INVOKEINTERFACE, Type.getType(this.predicateDesc).getInternalName(), "test", desc, true);
		visitor.visitJumpInsn(IFEQ, endOfLoopLabel); // if(!predicate.test(...)) continue;
		visitor.visitVarInsn(ret.getOpcode(ILOAD), temp);
		visitor.visitInsn(ret.getOpcode(IRETURN));
	}

	@Override
	public void emitEnd(MethodVisitor visitor,
			String className,
			String arrayFieldName,
			String arrayFieldDesc,
			int arrayLocalVarIndex,
			int arrayLengthLocalVarIndex) {
		Type type = this.methodType.getReturnType();
		int opcode = type.getOpcode(IRETURN);
		if(opcode != RETURN) {
			visitor.visitVarInsn(ALOAD, 0);
			if(this.function) {
				visitor.visitFieldInsn(GETFIELD, className, this.nullName, "L" + this.internalName + ";");
				this.emitInvoke(visitor);
			} else {
				visitor.visitFieldInsn(GETFIELD, className, this.nullName, this.methodType.getReturnType().getDescriptor());
			}
		}
		visitor.visitInsn(opcode);
	}

}
