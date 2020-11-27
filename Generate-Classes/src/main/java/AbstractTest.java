import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.ConstantsAbstracter;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import io.github.astrarre.abstracter.util.AbstracterLoader;

import net.minecraft.Bootstrap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings ({
		"ConstantConditions",
		"UnstableApiUsage",
		"ResultOfMethodCallIgnored"
})
public class AbstractTest {
	private static final Properties PROPERTIES = new Properties();

	static {
		try {
			
			InputStream stream = AbstractTest.class.getResourceAsStream("/gradle_info.properties");
			if(stream == null) {
				throw new IllegalStateException("Run this one more time (AbstractTest#main)");
			}
			PROPERTIES.load(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		// todo wait for player's TR patch to go on maven
		List<File> classpath = new ArrayList<>();
		for (String library : PROPERTIES.getProperty("libraries").split(";")) {
			File file = new File(library);
			AbstracterLoader.CLASSPATH.addURL(file.toURI().toURL());
			classpath.add(file);
		}

		AbstracterLoader.INSTANCE.addURL(new File(PROPERTIES.getProperty("minecraft")).toURI().toURL());

		// settings
		AbstracterConfig.registerInterface(AbstractBlock.Settings.class,
				c -> new InterfaceAbstracter(c, "v0/io/github/astrarre/block/IBlock$Settings"));

		AbstracterConfig.registerInnerOverride(Block.class, AbstractBlock.Settings.class);

		// attachment interfaces > extension methods, cus no javadoc
		AbstracterUtil.registerDefaultConstants(Blocks.class, Items.class);
		AbstracterConfig.registerConstants(Material.class,
				c -> new ConstantsAbstracter(c, "v0/io/github/astrarre/block/Materials"));

		AbstracterUtil.registerConstantlessInterface(Material.class);

		AbstracterUtil.registerDefaultInterface(Block.class,
				Item.class,
				ItemStack.class,
				Item.Settings.class,
				BlockState.class,
				BlockPos.class,
				World.class,
				WorldAccess.class,
				Entity.class,
				Enchantment.class,
				Bootstrap.class,
				StatusEffectInstance.class,
				MinecraftClient.class,
				ClientWorld.class,
				EntityType.class,
				MaterialColor.class,
				Vec3d.class,
				Vec3i.class,
				EntityPose.class);
		// base
		AbstracterUtil.registerDefaultBase(Block.class, Entity.class, Enchantment.class, Item.class, Material.class);

		File folder = new File(PROPERTIES.getProperty("projectDir"), "generated");
		folder.mkdirs();
		AbstracterUtil.apply(classpath,
				new File(folder, "api.jar").getAbsolutePath(),
				new File(folder, "api_sources.jar").getAbsolutePath(),
				new File(folder, "impl.jar").getAbsolutePath(),
				new File(folder, "manifest.properties").getAbsolutePath(),
				PROPERTIES.getProperty("mappings"));
	}
}
