package io.github.astrarre.itemview.v0.fabric;

import java.util.Objects;
import java.util.function.Function;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public interface FabricSerializers {
	Serializer<ItemStack> ITEM_STACK = Serializer.of(tag -> ItemStack.fromNbt(tag.asTag().toTag()), (stack) -> {
		NbtCompound tag = new NbtCompound();
		stack.writeNbt(tag);
		return FabricViews.view(tag);
	});
	Serializer<Identifier> IDENTIFIER = Serializer.of(tag -> new Identifier(tag.asString()),
			identifier -> NbtValue.of(NBTType.STRING, identifier.toString()));
	Serializer<BlockPos> BLOCK_POS = Serializer.of((tag) -> {
		NBTagView view = tag.asTag();
		return new BlockPos(view.getInt("x"), view.getInt("y"), view.getInt("z"));
	}, (pos) -> NBTagView.builder().putInt("x", pos.getX()).putInt("y", pos.getY()).putInt("z", pos.getZ()));
	Serializer<Text> TEXT = Serializer.of(value -> Text.Serializer.fromJson(value.asString()), text -> NbtValue.of(NBTType.STRING, Text.Serializer.toJson(text)));
	Serializer<Fluid> FLUID = of(Registry.FLUID);

	static Serializer<Entity> entity(World world) {
		return Serializer.of((s) -> EntityType.getEntityFromNbt(Objects.requireNonNull(s.asTag().toTag(), "no entry found!"), world)
				                            .orElseThrow(NullPointerException::new), (entity) -> {
			NbtCompound tag = new NbtCompound();
			entity.saveNbt(tag);
			return FabricViews.view(tag);
		});
	}

	@SuppressWarnings ("all")
	static <N extends NbtElement, T> Serializer<T> of(Function<N, T> reader, Function<T, N> writer) {
		return Serializer.of((Function) reader, (Function)writer);
	}

	static <T> Serializer<T> of(Registry<T> registry) {
		return Serializer.of((tag) -> registry.get(IDENTIFIER.read(tag)), (t) -> IDENTIFIER.save(registry.getId(t)));
	}
}
