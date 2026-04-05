package com.cake.azimuth.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A single lang key/default-English pair for datagen.
 *
 * <p>The key is resolved in order of precedence:</p>
 * <ol>
 *   <li>If {@link #source()} is set, the source class must carry
 *       {@link LangKeyFormat} and the key is formatted through it.</li>
 *   <li>If {@link #format()} is non-empty, the key is formatted through it.</li>
 *   <li>Otherwise the key is used as-is.</li>
 * </ol>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface LangDefault {
    String key();
    String value();
    Class<?> source() default void.class;
    String format() default "";
}
