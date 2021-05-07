package io.github.astrarre.itemview.internal.mixin.nbt;

import java.io.DataInput;
import java.io.DataOutput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.io.AbstractTag;

@Mixin(AbstractTag.class)
public interface AbstractTagAccess {
	@Invoker void callWrite(DataOutput dataOutput);
	@Invoker void callRead(DataInput dataOutput);
}
