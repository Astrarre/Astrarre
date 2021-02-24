import java.io.IOException;
import java.util.function.Predicate;

import cls.CompoundTagMethodFilter;
import cls.ItemStackAbs;
import cls.PacketByteBufAbs;
import cls.Vec3fAbs;
import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.AbstractAbstracter;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import io.github.astrarre.abstracter.func.elements.MethodSupplier;
import org.objectweb.asm.Type;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		AbstracterUtil util = AbstracterUtil.fromFile(args[0]);
		AbstracterConfig config = util.createConfig("astrarre_manifest");
		config.manualInterface(Identifier.class, "io/github/astrarre/util/v0/api/Id");
		config.manualInterface(Identifier.class, "io/github/astrarre/util/v0/api/Id");
		config.manualInterface(Sprite.class, "io/github/astrarre/rendering/v0/api/textures/SpriteInfo");
		config.registerInterface(Sprite.class);
		config.registerInterface(new PacketByteBufAbs());
		config.registerInterface(new ItemStackAbs());
		//config.registerInterface(PlayerEntity.class);
		//config.registerInterface(ServerPlayerEntity.class);
		//config.registerInterface(ClientPlayerEntity.class);

		config.registerInterface(Item.class);
		config.registerInterface(Registry.class);
		config.registerInterface(DefaultedRegistry.class);
		config.registerInterface(Block.class);
		config.registerInterface(BlockEntity.class);
		config.registerInterface(Entity.class);
		config.registerInterface(EntityType.class);
		config.registerInterface(EquipmentSlot.class);
		config.registerInterface(LivingEntity.class);
		config.registerInterface(BlockEntityType.class);

		// todo NBT aware stack sizes and durability
		config.getInterfaceAbstraction(Type.getInternalName(Item.class)).post((config1, cls, node, impl) -> {
			node.interfaces.add("io/github/astrarre/itemview/v0/api/item/ItemKey");
		});


		AbstractAbstracter abstracter = config.getInterfaceAbstraction(Type.getInternalName(Block.class));
		abstracter.addInner(config.registerInterface(AbstractBlock.Settings.class).name(abstracter.name + "$Settings"));

		config.registerInterface(BlockState.class);
		config.registerInterface(World.class);


		config.registerInterface(BlockPos.class);
		config.registerInterface(Vec3d.class);
		config.registerInterface(new Vec3fAbs());


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