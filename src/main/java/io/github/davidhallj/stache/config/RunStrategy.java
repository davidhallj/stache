package io.github.davidhallj.stache.config;

import io.github.davidhallj.stache.config.advanced.CacheReadStrategy;
import io.github.davidhallj.stache.config.advanced.CacheWriteStrategy;
import io.github.davidhallj.stache.config.advanced.CachingStrategy;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RunStrategy {

    /**
     * Intended only for use during development. Will always call the live remove service, and never read from
     * or write to the cache.
     *
     * As this is calling a live service, it should never be turned on for real unit test runs
     */
    DEV_MODE(
            CacheWriteStrategy.OFF,
            CacheReadStrategy.OFF,
            CachingStrategy.STATELESS  // Caching isn't needed at all for this config
    ),
    /**
     * This mode provides the main Smart Mock functionality to your tests.
     *
     * First test run will run against all live services, and cache all data.
     * Second test run will only read from local cached data
     *
     */
    SMART_CACHE_MODE(
            CacheWriteStrategy.ON,
            CacheReadStrategy.SMART,
            CachingStrategy.STATELESS
    ),
    /**
     * This mode provides the most safety if you intend to allow Stache during unit test execution
     */
    READ_ONLY_MODE(
            CacheWriteStrategy.OFF,
            CacheReadStrategy.ALWAYS,
            CachingStrategy.STATELESS
    );

    private final CacheWriteStrategy cacheWriteStrategy;
    private final CacheReadStrategy cacheReadStrategy;
    private final CachingStrategy cachingStrategy;
    //private final ExceptionResolverStrategy exceptionResolverStrategy;

}
