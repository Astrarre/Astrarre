import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import io.netty.buffer.Unpooled;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class Main implements Opcodes {
	public static void main(String[] args) throws IOException, InterruptedException {
		AbstracterUtil.applyParallel(args[0], () -> {
			AbstracterConfig.registerInterface(new InterfaceAbstracter(PacketByteBuf.class).post((cls, node, impl) -> {
				MethodNode newInstance = new MethodNode(ACC_PUBLIC | ACC_STATIC, "newInstance", "()L" + node.name + ";", null, null);
				newInstance.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Unpooled.class), "buffer", "()Lio/netty/buffer/ByteBuf;");
				newInstance.visitMethodInsn(INVOKESTATIC, node.name, "newInstance", String.format("(Lio/netty/buffer/ByteBuf;)L%s;", node.name));
				newInstance.visitInsn(ARETURN);
				node.methods.add(newInstance);
			}));

			AbstracterConfig.registerInterface(new InterfaceAbstracter(Inventory.class).post((aClass, node, b) -> {
				node.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "EMPTY", "Lio/github/astrarre/v0/inventory/Inventory;", null, null);
				if (b) {
					for (MethodNode method : node.methods) {
						if (method.name.equals("astrarre_artificial_clinit")) {
							method.visitTypeInsn(NEW, "io/github/astrarre/itemview/internal/EmptyInventory");
							method.visitInsn(DUP);
							method.visitMethodInsn(INVOKEVIRTUAL, "io/github/astrarre/itemview/internal/EmptyInventory", "<init>", "()V");
							method.visitFieldInsn(PUTSTATIC,
									"io/github/astrarre/v0/inventory/Inventory",
									"EMPTY",
									"Lio/github/astrarre/v0/inventory/Inventory;");
						}
					}
				}

				MethodVisitor visitor = node.visitMethod(ACC_PUBLIC, "isInventoryDynamic", "()Z", null, null);
				visitor.visitInsn(ICONST_0);
				visitor.visitInsn(IRETURN);
			}));
			// todo add casting with default return values?
			// todo replace InventoryProvider with direction based one?
			AbstracterUtil.registerDefaultInterface(Item.class,
					Block.class,
					BlockEntity.class,
					BlockState.class,
					World.class,
					Entity.class,
					BlockPos.class,
					WorldAccess.class,
					Box.class,
					Direction.class);
			AbstracterUtil.registerDefaultInterface(ItemStack.class);
			AbstracterConfig.registerInterface(new InterfaceAbstracter(ItemStack.class).post((aClass, node, b) -> {
				node.interfaces.add("io/github/astrarre/itemview/v0/api/item/ItemStackView");
			}));
			AbstracterUtil.registerDefaultInterface(MinecraftServer.class);
		});
		System.out.println("DONE");
	}
}
