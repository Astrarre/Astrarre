package io.github.astrarre.components.internal.mixin;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.DataObjectHolder;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.github.astrarre.itemview.v0.api.Serializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin (BlockEntity.class)
public class BlockEntityMixin implements DataObjectHolder {
	// todo
	public CopyAccess astrarre_access;
	public int astrarre_version;

	@Override
	public CopyAccess astrarre_getObject() {
		return this.astrarre_access;
	}

	@Override
	public int astrarre_getVersion() {
		return this.astrarre_version;
	}

	@Override
	public void astrarre_setObject(CopyAccess object, int version) {
		this.astrarre_access = object;
		this.astrarre_version = version;
	}

	@Inject(method = "writeNbt", at = @At("RETURN"))
	public void writeNbt(NbtCompound nbt, CallbackInfo cir) {
		nbt.put("astrarre_components", ComponentsInternal.write((BlockEntity) (Object) this, ComponentsInternal.SERIALIZE_BLOCK_ENTITY_INTERNAL));
	}

	@Inject(method = "readNbt", at = @At("RETURN"))
	public void readNbt(NbtCompound nbt, CallbackInfo ci) {
		ComponentsInternal.read(nbt.getCompound("astrarre_components"), (BlockEntity) (Object) this, ComponentsInternal.SERIALIZE_BLOCK_ENTITY_INTERNAL);
	}
}
