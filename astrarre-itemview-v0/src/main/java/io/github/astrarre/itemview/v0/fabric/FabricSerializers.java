package io.github.astrarre.itemview.v0.fabric;

import java.util.Objects;
import java.util.Optional;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface FabricSerializers {
	Serializer<ItemStack> ITEM_STACK = Serializer.of((tag, s) -> ItemStack.fromTag(tag.getTag(s).toTag()), (output, s, stack) -> {
		CompoundTag tag = new CompoundTag();
		stack.toTag(tag);
		output.putTag(s, FabricViews.view(tag));
	});
	Serializer<Identifier> IDENTIFIER = Serializer.of((tag, s) -> new Identifier(tag.getString(s)), (tag, s, identifier) -> tag.putString(s, identifier.toString()));
	Serializer<BlockPos> BLOCK_POS = Serializer.of((tag, s) -> {
		NBTagView view = tag.getTag(s);
		return new BlockPos(view.getInt("x"), view.getInt("y"), view.getInt("z"));
	}, (tag, s, pos) -> tag.putTag(s, NBTagView.builder().putInt("x", pos.getX()).putInt("y", pos.getY()).putInt("z", pos.getZ())));



	static Serializer<Optional<Entity>> entity(World world) {
		return Serializer.of(
				(s, t) -> EntityType.getEntityFromTag(Objects.requireNonNull(s.getTag(t).toTag(), "no entry found!"), world),
				(out, key, entity) -> {
					if (entity.isPresent()) {
						CompoundTag tag = new CompoundTag();
						entity.get().saveToTag(tag);
						out.putTag(key, FabricViews.view(tag));
					}
				});
	}
}
