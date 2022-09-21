package io.github.davidhallj.stache.config.advanced;

public enum CacheNamingStrategy {
    /**
     * Each test method will create its own subdirectory in the main cache,
     * named after itself.
     *
     * Note: requires a hook into the junit framework in order to receive the current
     * executing test method name. Must run the test class with the StacheJunitRunner
     *
     * Each unit test will be isolated to only using its own cache directory
     */
    METHOD_SCOPED,
    /**
     * Each Stache instance will write to its own subdirectory in the main cache, named after
     * itself
     *
     * Potential to share cached data between different unit tests
     */
    MOCK_SCOPED,
    /**
     * Each test method will write to the same directory, specified by 'cacheRootDir' config value
     */
    STATIC_SCOPED,
    /**
     * TODO need to implement this
     */
    TEST_SUITE_SCOPED


}
