import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import io.netty.buffer.Unpooled;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

public class Main implements Opcodes {
	public static void main(String[] args) throws IOException {
		AbstracterUtil.applyParallel(args[0], () -> {
			AbstracterConfig.registerInterface(new InterfaceAbstracter(PacketByteBuf.class).post((cls, node, impl) -> {
				MethodNode newInstance = new MethodNode(ACC_PUBLIC | ACC_STATIC, "newInstance", "()L" + node.name + ";", null, null);
				newInstance.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Unpooled.class), "buffer", "()Lio/netty/buffer/ByteBuf;");
				newInstance.visitMethodInsn(INVOKESTATIC, node.name, "newInstance", String.format("(Lio/netty/buffer/ByteBuf;)L%s;", node.name));
				newInstance.visitInsn(ARETURN);
				node.methods.add(newInstance);
			}));
			AbstracterUtil.registerDefaultInterface(MinecraftServer.class);
		});
	}
}
