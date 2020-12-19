import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.ConstantsAbstracter;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import io.github.astrarre.abstracter.util.AbstracterLoader;
import org.reflections.Reflections;

import net.minecraft.Bootstrap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings ("ResultOfMethodCallIgnored")
public class AbstractTest {
	private static final Reflections REFLECTIONS = new Reflections();
	private static final Properties PROPERTIES = new Properties();

	static {
		try {
			InputStream stream = AbstractTest.class.getResourceAsStream("/gradle_info.properties");
			if (stream == null) {
				throw new IllegalStateException("Run this one more time (AbstractTest#main)");
			}
			PROPERTIES.load(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		// todo wait for player's TR patch to go on maven
		for (String library : PROPERTIES.getProperty("libraries").split(";")) {
			File file = new File(library);
			AbstracterLoader.CLASSPATH.addURL(file.toURI().toURL());
		}

		AbstracterLoader.INSTANCE.addURL(new File(PROPERTIES.getProperty("minecraft")).toURI().toURL());

		// settings
		AbstracterConfig.registerInterface(new InterfaceAbstracter(AbstractBlock.Settings.class,
				"v0/io/github/astrarre/block/Block$Settings"));

		AbstracterConfig.registerInnerOverride(Block.class, AbstractBlock.Settings.class);

		// attachment interfaces > extension methods, cus no javadoc
		AbstracterUtil.registerDefaultConstants(Blocks.class, Items.class);

		AbstracterConfig.registerConstants(new ConstantsAbstracter(Material.class,
				"v0/io/github/astrarre/block/MinecraftMaterials"));
		AbstracterUtil.registerConstantlessInterface(Material.class);
		registerSubclassBaseInterface(Block.class);
		registerSubclassBaseInterface(Item.class);
		registerSubclassBaseInterface(Enchantment.class);
		registerSubclassBaseInterface(Entity.class);
		registerSubclassBaseInterface(BlockEntity.class);
		registerSubclassInterface(Tag.class);
		AbstracterUtil.registerDefaultInterface(Block.class,
				ItemStack.class,
				Item.Settings.class,
				BlockState.class,
				BlockPos.class,
				World.class,
				WorldAccess.class,
				Bootstrap.class,
				StatusEffectInstance.class,
				MinecraftClient.class,
				ClientWorld.class,
				EntityType.class,
				MaterialColor.class,
				Vec3d.class,
				Vec3i.class,
				EntityPose.class,
				SaddledComponent.class,
				ItemSteerable.class);
		// base
		AbstracterUtil.registerDefaultBase(Material.class);

		File folder = new File(PROPERTIES.getProperty("projectDir"), "generated");
		File include = new File(folder, "include");
		include.mkdirs();


		AbstracterUtil.applyParallel(new File(folder, "api.jar").getAbsolutePath(),
				new File(folder, "api_sources.jar").getAbsolutePath(),
				new File(folder, "impl.jar").getAbsolutePath(),
				new File(include, "manifest.properties").getAbsolutePath(),
				PROPERTIES.getProperty("mappings"));
		AbstracterConfig.writeBaseManifest(new FileOutputStream(new File(include, "base_manifest.properties")));
	}

	private static void registerSubclassBaseInterface(Class<?> sup) {
		AbstracterUtil.registerDefaultInterface(sup);
		AbstracterUtil.registerDefaultBase(sup);
		for (Class<?> cls : REFLECTIONS.getSubTypesOf(sup)) {
			AbstracterUtil.registerDefaultInterface(cls);
			AbstracterUtil.registerDefaultBase(cls);
		}
	}

	private static void registerSubclassInterface(Class<?> cls) {
		AbstracterUtil.registerDefaultInterface(cls);
		for (Class<?> sub : REFLECTIONS.getSubTypesOf(cls)) {
			AbstracterUtil.registerDefaultInterface(sub);
		}
	}
}
