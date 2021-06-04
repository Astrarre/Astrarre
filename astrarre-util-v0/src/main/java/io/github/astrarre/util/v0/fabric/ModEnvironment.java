package io.github.astrarre.util.v0.fabric;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * with {@link ModDependentMixin} as the mixin plugin, adding this annotation to a mixin will cause it to not be applied if the mod is not present
 */
@Retention(RetentionPolicy.CLASS)
public @interface ModEnvironment {
	/**
	 * @return the modid
	 */
	String value();
}
