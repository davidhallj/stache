package io.github.davidhallj.stache.config;

import io.github.davidhallj.stache.config.advanced.CacheNamingStrategy;
import io.github.davidhallj.stache.exceptionmapping.ExceptionResolver;
import io.github.davidhallj.stache.exceptionmapping.WebExceptionResolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Defaults {

    public static final ExceptionResolver WEB_EXCEPTION_RESOLVER = new WebExceptionResolver();
    // This string should actually be a full Path object, but the path object is not allowed to be set as a default
    // value on the @Advanced annotation.. need a solution for this
    public static final String MAVEN_TEST_RESOURCES = "src/test/resources"; // assumes maven
    //public static final Path MAVEN_TEST_RESOURCES  = Path.of("src", "test", "resources"); // assumes maven
    public static final String CACHE_DIR  = "cache";
    public static final CacheNamingStrategy CACHE_NAMING_STRATEGY = CacheNamingStrategy.METHOD_SCOPED;

}
