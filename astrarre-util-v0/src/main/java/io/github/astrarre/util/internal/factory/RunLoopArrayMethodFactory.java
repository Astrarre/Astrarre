package io.github.astrarre.util.internal.factory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class RunLoopArrayMethodFactory extends AbstractLoopArrayMethodFactory {
	public RunLoopArrayMethodFactory(Method method) {
		super(method);
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
		visitor.visitVarInsn(ALOAD, functionLocalVarIndex); // get value from array
		this.emitInvoke(visitor);
		this.emitExit(visitor,
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

	public void emitInvoke(MethodVisitor visitor) {
		int index = 0;
		for(Type argumentType : this.methodType.getArgumentTypes()) {
			visitor.visitVarInsn(argumentType.getOpcode(ILOAD), index + 1); // skip `this`
			index += argumentType.getSize();
		}
		boolean iface = Modifier.isInterface(this.type.getModifiers());
		if(iface) {
			visitor.visitMethodInsn(INVOKEINTERFACE,
			                        Type.getInternalName(this.type),
			                        this.getMethod().getName(),
			                        this.methodType.getDescriptor(),
			                        true);
		} else {
			visitor.visitMethodInsn(INVOKEVIRTUAL,
			                        Type.getInternalName(this.type),
			                        this.getMethod().getName(),
			                        this.methodType.getDescriptor(),
			                        false);
		}
	}

	@Override
	public void emitEnd(MethodVisitor visitor,
			String className,
			String arrayFieldName,
			String arrayFieldDesc,
			int arrayLocalVarIndex,
			int arrayLengthLocalVarIndex) {
		int opcode = this.methodType.getReturnType().getOpcode(IRETURN);
		if(opcode == IRETURN) {
			visitor.visitInsn(ICONST_0);
		} else if(opcode == ARETURN) {
			visitor.visitInsn(ACONST_NULL);
		} else if(opcode == DRETURN) {
			visitor.visitInsn(DCONST_0);
		} else if(opcode == FRETURN) {
			visitor.visitInsn(FCONST_0);
		} else if(opcode == LRETURN) {
			visitor.visitInsn(LCONST_0);
		}
		visitor.visitInsn(opcode);
	}

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
		if(!this.methodType.getReturnType().equals(Type.VOID_TYPE)) {
			visitor.visitInsn(POP);
		}
	}
}
