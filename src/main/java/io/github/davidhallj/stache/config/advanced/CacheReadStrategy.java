package io.github.davidhallj.stache.config.advanced;

public enum CacheReadStrategy {
    /**
     * Will never attempt to read from cache
     */
    OFF,
    /**
     * If cache data is there, it will attempt to read it. If no cache data is found, execution will be
     * allowed to continue and
     */
    SMART,
    /**
     * If no cache data is found, will stop test execution and throw Exception
     */
    ALWAYS
}
