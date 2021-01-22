import java.io.IOException;
import java.util.function.Predicate;

import cls.CompoundTagMethodFilter;
import cls.InventoryAbs;
import cls.ItemStackAbs;
import cls.PacketByteBufAbs;
import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.AbstractAbstracter;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import io.github.astrarre.abstracter.func.elements.MethodSupplier;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class Main implements Opcodes {
	public static void main(String[] args) throws IOException, InterruptedException {
		AbstracterUtil util = AbstracterUtil.fromFile(args[0]);
		AbstracterConfig config = util.createConfig("astrarre_manifest");
		config.registerInterface(new PacketByteBufAbs());
		config.registerInterface(new InventoryAbs());
		config.registerInterface(new ItemStackAbs());

		all(config, "net.minecraft.item", Item.class);

		// todo NBT aware stack sizes and durability
		config.getInterfaceAbstraction(Type.getInternalName(Item.class)).post((config1, cls, node, impl) -> {
			node.interfaces.add("io/github/astrarre/itemview/v0/api/item/ItemKey");
		});

		all(config, "net.minecraft.block", Block.class);

		AbstractAbstracter abstracter = config.getInterfaceAbstraction(Type.getInternalName(Block.class));
		abstracter.addInner(config.registerInterface(AbstractBlock.Settings.class).name(abstracter.name + "$Settings"));

		all(config, "net.minecraft.block.entity", BlockEntity.class);
		all(config, "net.minecraft.entity", Entity.class, e -> e != EnderDragonEntity.class && e != EnderDragonPart.class);

		config.registerInterface(BlockState.class);
		config.registerInterface(World.class);
		config.registerInterface(BlockPos.class);
		config.registerInterface(WorldAccess.class);
		config.registerInterface(Box.class);
		config.registerInterface(MinecraftServer.class);
		config.registerInterface(ItemSteerable.class);
		config.registerInterface(Fluid.class);
		config.registerInterface(ItemConvertible.class);
		config.registerInterface(new InterfaceAbstracter(CompoundTag.class).methods(MethodSupplier.INTERFACE_DEFAULT.filtered(new CompoundTagMethodFilter())))
		      .post((config12, cls, node, impl) -> {
			      node.interfaces.add("io/github/astrarre/itemview/v0/api/nbt/NBTagView");
		      });

		// constants
		config.registerInterface(Direction.class);
		config.registerInterface(Fluids.class);
		config.registerInterface(Items.class);
		config.registerInterface(Blocks.class);
		config.registerInterface(Material.class); // todo seperate into Material and Materials for consistency yay
		util.write(config);
	}

	private static Reflections all(AbstracterConfig config, String pkg, Class<?> type) {
		return all(config, pkg, type, c -> true);
	}

	private static Reflections all(AbstracterConfig config, String pkg, Class<?> type, Predicate<Class<?>> baseAbstract) {
		config.registerBase(type);
		config.registerInterface(type);

		Reflections reflections = new Reflections(ClasspathHelper.forPackage(pkg, config.minecraft));
		reflections.getSubTypesOf(type).forEach(c -> {
			config.registerInterface(c);
			if (baseAbstract.test(c)) {
				config.registerBase(c);
			}
		});
		return reflections;
	}
}
