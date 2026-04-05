package com.cake.azimuth.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares one or more default lang entries for datagen.
 * Can be placed on classes or methods to co-locate lang definitions
 * with the code that uses them.
 *
 * <p>When {@link #modid()} is empty, the owning mod is auto-detected
 * by matching the annotated class's package against known {@code @Mod}
 * entry points on the classpath.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IncludeLangDefaults {
    LangDefault[] value();
    String modid() default "";
}
