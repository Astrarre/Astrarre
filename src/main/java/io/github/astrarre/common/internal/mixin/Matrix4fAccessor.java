package io.github.astrarre.common.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.math.Matrix4f;

@Mixin (Matrix4f.class)
public interface Matrix4fAccessor {
	@Accessor("a00")
	float getA00();

	@Accessor("a01")
	float getA01();

	@Accessor("a02")
	float getA02();

	@Accessor("a03")
	float getA03();

	@Accessor("a10")
	float getA10();

	@Accessor("a11")
	float getA11();

	@Accessor("a12")
	float getA12();

	@Accessor("a13")
	float getA13();

	@Accessor("a20")
	float getA20();

	@Accessor("a21")
	float getA21();

	@Accessor("a22")
	float getA22();

	@Accessor("a23")
	float getA23();

	@Accessor("a30")
	float getA30();

	@Accessor("a31")
	float getA31();

	@Accessor("a32")
	float getA32();

	@Accessor("a33")
	float getA33();
}
