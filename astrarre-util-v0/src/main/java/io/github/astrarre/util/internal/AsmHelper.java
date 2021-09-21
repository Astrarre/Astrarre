package io.github.astrarre.util.internal;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class AsmHelper {
	public static void emitWrapInstruction(MethodVisitor visitor, int sort) {
		if(sort == Type.DOUBLE) {
			visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
		} else if(sort == Type.FLOAT) {
			visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		} else if(sort == Type.CHAR) {
			visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
		} else if(sort == Type.BOOLEAN) {
			visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
		} else if(sort == Type.LONG) {
			visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
		} else if(sort == Type.INT) {
			visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
		} else if(sort == Type.SHORT) {
			visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
		} else if(sort == Type.BYTE) {
			visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
		}
	}
}
