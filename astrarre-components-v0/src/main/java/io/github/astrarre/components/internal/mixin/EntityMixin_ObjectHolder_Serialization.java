package io.github.astrarre.components.internal.mixin;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;
import io.github.astrarre.components.v0.api.Copier;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.DataObjectHolder;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.github.astrarre.components.v0.fabric.FabricSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

@Mixin(Entity.class)
public class EntityMixin_ObjectHolder_Serialization implements DataObjectHolder {
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
	public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
		NbtCompound componentData = new NbtCompound();
		for (Map.Entry<String, Pair<Component<Entity, ?>, FabricSerializer<?, ?>>> entry : ComponentsInternal.SERIALIZE_ENTITY_INTERNAL.entrySet()) {
			Pair<Component<Entity, ?>, FabricSerializer<?, ?>> value = entry.getValue();
			componentData.put(entry.getKey(), FabricComponents.serialize((Entity) (Object) this, (Component)value.getFirst(), value.getSecond()));
		}
		nbt.put("astrarre_components", componentData);
	}

	@Inject(method = "readNbt", at = @At("RETURN"))
	public void readNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtCompound componentData = nbt.getCompound("astrarre_components");
		for (String key : componentData.getKeys()) {
			Pair<Component<Entity, ?>, FabricSerializer<?, ?>> pair = ComponentsInternal.SERIALIZE_ENTITY_INTERNAL.get(key);
			if(pair != null) {
				FabricComponents.deserialize(componentData.get(key), (Entity) (Object) this, (Component) pair.getFirst(), (FabricSerializer) pair.getSecond());
			}
			// name changed, perhaps some DFU stuff later(?)
		}
	}

	@Inject(method = "copyFrom", at = @At("RETURN"))
	public void copyFrom(Entity original, CallbackInfo ci) {
		for (Pair<Component<Entity, ?>, Copier<?>> pair : ComponentsInternal.COPY_ENTITY_INTENRAL) {
			FabricComponents.copy(original, (Entity) (Object) this, (Component)pair.getFirst(), pair.getSecond());
		}
	}
}
