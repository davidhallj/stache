package io.github.davidhallj.stache.config;

import io.github.davidhallj.stache.config.advanced.CacheNamingStrategy;
import io.github.davidhallj.stache.core.StacheStaticContext;
import io.github.davidhallj.stache.exceptionmapping.ExceptionResolver;
import io.github.davidhallj.stache.jaxrs.JaxrsFactory;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StacheRunConfiguration {

    /**
     * Top level Stache params
     */
    private final RunStrategy runStrategy;
    /**
     * Advanced params
     *
     * Question: should these default here or in the annotation?
     */
    private final String testResourceDir;
    private final String cacheDir;
    private final CacheNamingStrategy cacheNamingStrategy;

    /**
     * Experimental params
     * TODO actually add these options ot the @Advanced annotation
     */
    @Builder.Default
    private final JaxrsFactory jaxrsFactory = StacheStaticContext.JAXRS_FACTORY;
    @Builder.Default
    private final Gson gson = StacheStaticContext.GSON;

    @Builder.Default
    private final boolean cacheExceptions = true;
    @Builder.Default
    private final ExceptionResolver exceptionResolver = Defaults.WEB_EXCEPTION_RESOLVER;

}
