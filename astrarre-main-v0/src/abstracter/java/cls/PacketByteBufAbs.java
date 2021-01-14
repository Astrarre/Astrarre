package cls;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import io.netty.buffer.Unpooled;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class PacketByteBufAbs extends InterfaceAbstracter {
	public PacketByteBufAbs(Class<?> cl) {
		super(cl);
		this.post(PacketByteBufAbs::process);
	}

	private static void process(AbstracterConfig c, Class<?> cls, ClassNode node, boolean impl) {
		MethodNode newInstance = new MethodNode(ACC_PUBLIC | ACC_STATIC, "newInstance", "()L" + node.name + ";", null, null);
		newInstance.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Unpooled.class), "buffer", "()Lio/netty/buffer/ByteBuf;");
		newInstance.visitMethodInsn(INVOKESTATIC, node.name, "newInstance", String.format("(Lio/netty/buffer/ByteBuf;)L%s;", node.name));
		newInstance.visitInsn(ARETURN);
		node.methods.add(newInstance);
	}
}
