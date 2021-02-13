package util;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.func.post.PostProcessor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ExtensionMethod implements PostProcessor, Opcodes {
	public final int access;
	public final String owner, name, desc;

	/**
	 * The method this targets must be static whether or not the extension method is static
	 *
	 * @param access the access flags of the method that will be added
	 */
	public ExtensionMethod(int access, String owner, String name, String desc) {
		this.access = access;
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	@Override
	public void process(AbstracterConfig config, Class<?> aClass, ClassNode node, boolean b) {
		Type type = Type.getMethodType(this.desc);
		Type[] args = type.getArgumentTypes();
		boolean isInstance = !Modifier.isStatic(this.access);
		if (isInstance) {
			args = Arrays.copyOfRange(args, 1, args.length);
		}

		MethodNode method = new MethodNode(this.access, this.name, Type.getMethodType(type.getReturnType(), args).getDescriptor(), null, null);
		node.methods.add(method);
		if (b && !Modifier.isAbstract(this.access)) {
			int varIndex = 0;
			if (isInstance) {
				method.visitVarInsn(ALOAD, varIndex++);
			}

			for (Type arg : args) {
				method.visitVarInsn(arg.getOpcode(ILOAD), varIndex);
				varIndex += arg.getSize();
			}

			method.visitMethodInsn(INVOKESTATIC, this.owner, this.name, this.desc);
			method.visitInsn(type.getReturnType().getOpcode(IRETURN));
		}
	}
}
