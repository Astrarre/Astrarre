package io.github.astrarre.util.v0.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.jetbrains.annotations.ApiStatus;

/**
 * States that an api is an edge api, this means backwards compatibility is not guaranteed.
 *
 * This is often used for apis that are tightly coupled to minecraft's api, either because it requires a minecraft class as a parameter, or
 * it uses minecraft's systems in a way that is not well abstracted.
 */
@ApiStatus.Experimental
@Retention(RetentionPolicy.SOURCE)
public @interface Edge {
	String reason() default "";
}
