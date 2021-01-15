import java.io.IOException;

import cls.InventoryAbs;
import cls.PacketByteBufAbs;
import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import io.netty.buffer.Unpooled;
import joptsimple.internal.Reflection;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemSteerable;
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
		AbstracterConfig config = new AbstracterConfig();
		AbstracterUtil.applyParallel(config, args[0], () -> {
			config.registerInterface(new PacketByteBufAbs(PacketByteBuf.class));

			config.registerInterface(new InventoryAbs(Inventory.class));
			all(config, "net.minecraft.item", Item.class);
			all(config, "net.minecraft.block", Block.class);
			all(config, "net.minecraft.block.entity", BlockEntity.class);

			config.registerInterface(BlockState.class);
			config.registerInterface(World.class);
			config.registerInterface(Entity.class);
			config.registerInterface(BlockPos.class);
			config.registerInterface(WorldAccess.class);
			config.registerInterface(Box.class);
			config.registerInterface(Direction.class);
			config.registerInterface(MinecraftServer.class);
			config.registerInterface(ItemSteerable.class);

			config.registerInterface(new InterfaceAbstracter(ItemStack.class).post((c, aClass, node, b) -> {
				node.interfaces.add("io/github/astrarre/itemview/v0/api/item/ItemStackView");
			}));

		});
		System.out.println("DONE");
	}

	private static Reflections all(AbstracterConfig config, String pkg, Class<?> type) {
		config.registerBase(type);
		config.registerInterface(type);

		Reflections reflections = new Reflections(ClasspathHelper.forPackage(pkg, config.minecraft));
		reflections.getSubTypesOf(type).forEach(c -> {
			config.registerInterface(c);
			config.registerBase(c);
		});
		return reflections;
	}
}
