package io.github.astrarre.abstraction.internal.astrarre;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.function.IntPredicate;

import com.chocohead.mm.api.ClassTinkerers;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BaseAbstractionApplicator implements Opcodes {
	public static void apply(String abstraction, String minecraft, boolean named) {
		ClassTinkerers.addTransformation(abstraction, node -> {
			node.superName = minecraft;
			// todo remap bridges
			if (named) {
				deleteUseless(node.methods.iterator(), i -> i == INVOKESPECIAL, false);
			}
		});
	}

	public static void deleteUseless(Iterator<MethodNode> iterator, IntPredicate opcode, boolean isInterface) {
		outer:
		while (iterator.hasNext()) {
			MethodNode method = iterator.next();
			InsnList list = method.instructions;
			Type methodType = Type.getMethodType(method.desc);
			Type[] parameters = methodType.getArgumentTypes();


			int offset = Modifier.isStatic(method.access) ? 0 : (isInterface ? 2 : 1);
			// load this & parameters + invoke + return
			if (list.size() != (parameters.length + 2 + offset)) {
				continue;
			}

			if (offset > 0 && !isVar(list.get(0), 0, ALOAD)) {
				continue;
			}

			if (isInterface && offset == 2) {
				AbstractInsnNode node = list.get(1);
				if (!(node instanceof TypeInsnNode && node.getOpcode() == CHECKCAST)) {
					continue;
				}
			}

			int locals = Math.min(offset, 1);
			for (int i = 0; i < parameters.length; i++) {
				int index = i + offset;
				if (!isVar(list.get(index), locals, parameters[i].getOpcode(ILOAD))) {
					continue outer;
				}
				locals += parameters[i].getSize();
			}

			AbstractInsnNode invoke = list.get(offset + parameters.length);
			if (opcode.test(invoke.getOpcode())) {
				MethodInsnNode invokeMethod = (MethodInsnNode) invoke;
				if (!method.desc.equals(invokeMethod.desc) || !method.name.equals(invokeMethod.name)) {
					continue;
				}
			} else {
				continue;
			}

			AbstractInsnNode retNode = list.getLast();
			if (retNode.getOpcode() == methodType.getReturnType().getOpcode(IRETURN)) {
				if(isInterface) {
					method.access |= ACC_ABSTRACT;
					method.instructions.clear();
				} else {
					iterator.remove();
				}
			}
		}
	}

	private static boolean isVar(AbstractInsnNode insn, int index, int opcode) {
		if (insn instanceof VarInsnNode) {
			VarInsnNode varNode = (VarInsnNode) insn;
			return varNode.var == index && varNode.getOpcode() == opcode;
		}
		return false;
	}
}
