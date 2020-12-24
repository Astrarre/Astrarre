package io.github.astrarre.v0.api.gui.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * a value that is not synced to the server
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ClientOnlyProperty {}
