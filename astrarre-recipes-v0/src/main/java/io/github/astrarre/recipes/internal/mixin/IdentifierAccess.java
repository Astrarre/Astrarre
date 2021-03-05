package io.github.astrarre.recipes.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.Identifier;

@Mixin(Identifier.class)
public interface IdentifierAccess {
	@Invoker
	static boolean callIsNamespaceCharacterValid(char c) {
		throw new IllegalStateException("mixin did not apply!");
	}
}
