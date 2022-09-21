package io.github.davidhallj.stache.annotations;

import io.github.davidhallj.stache.config.RunStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface JaxrsStache {

    String url();

    RunStrategy runConfig() default RunStrategy.SMART_CACHE_MODE;

    Advanced advanced() default @Advanced;

}
