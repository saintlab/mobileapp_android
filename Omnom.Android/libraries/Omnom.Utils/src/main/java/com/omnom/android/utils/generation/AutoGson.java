package com.omnom.android.utils.generation;

/**
 * Created by Ch3D on 25.12.2014.
 */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks an {@link auto.parcel.AutoParcel @AutoValue}-annotated type for proper Gson serialization.
 * <p/>
 * This annotation is needed because the {@linkplain Retention retention} of {@code @AutoValue}
 * does not allow reflection at runtime.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface AutoGson {
}
