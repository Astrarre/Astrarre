package io.github.astrarre.common.internal;

import com.chocohead.mm.api.ClassTinkerers;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class InterfaceAbstractionApplicator implements Opcodes {
	public static void apply(String minecraft, String abstractionClass, boolean isNamed) {
		ClassTinkerers.addTransformation(minecraft, node -> {
			node.interfaces.add(abstractionClass);
			MethodNode method = node.methods.stream().filter(n -> n.name.equals("<clinit>")).findFirst().orElseGet(() -> {
				MethodNode n = new MethodNode(ACC_STATIC, "<clinit>", "()V", null, null);
				n.visitInsn(RETURN);
				return n;
			});
			InsnList list = method.instructions;
			list.insertBefore(list.getLast(), new MethodInsnNode(INVOKESTATIC, abstractionClass, "astrarre_artificial_clinit", "()V"));

			if(isNamed) {
				BaseAbstractionApplicator.deleteUseless(node.methods.iterator(), i -> i == INVOKEVIRTUAL || i == INVOKEINTERFACE);
			}
		});
	}
}
