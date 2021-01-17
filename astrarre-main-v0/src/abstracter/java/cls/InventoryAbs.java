package cls;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.inventory.Inventory;

public class InventoryAbs extends InterfaceAbstracter {
	public InventoryAbs() {
		super(Inventory.class);
		this.post(InventoryAbs::process);
	}

	private static void process(AbstracterConfig c, Class<?> aClass, ClassNode node, boolean b) {
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
	}
}
