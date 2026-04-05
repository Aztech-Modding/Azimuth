package com.cake.azimuth.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a lang key format template for a class. When referenced as a
 * {@link LangDefault#source()}, the short key is inserted into this format
 * via {@link String#format(String, Object...)}.
 *
 * <p>Example: {@code @LangKeyFormat("gui.assembly.exception.%s")} on a class
 * means a {@code @LangDefault(key = "too_heavy", source = ThatClass.class, ...)}
 * resolves to {@code "gui.assembly.exception.too_heavy"}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LangKeyFormat {
    String value();
}
