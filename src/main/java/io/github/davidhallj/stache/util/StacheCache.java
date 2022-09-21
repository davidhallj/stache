package io.github.davidhallj.stache.util;

import io.github.davidhallj.stache.config.StacheConfiguration;
import io.github.davidhallj.stache.config.advanced.CacheNamingStrategy;
import io.github.davidhallj.stache.exception.StacheException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Slf4j
public class StacheCache {

    private final Path cacheRoot;

    public StacheCache(StacheConfiguration config, Class<?> cls) {
        this.cacheRoot = resolveCachePath(config, cls, config.getTestContext().getTestMethodName());
    }

    public void writeCacheFile(String fileName, String content) throws IOException {

        createCacheDirIfEmpty();

        final Path cacheFilePath = cacheRoot.resolve(fileName);

        log.info("Write cache file: {}", cacheFilePath);

        Files.writeString(
                cacheFilePath,
                content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public String readCacheFile(String fileName) throws IOException {
        Path cacheFilePath = cacheRoot.resolve(fileName);

        log.info("Read cache file: {}", cacheFilePath);

        try {

            String fileContents = Files.readString(cacheFilePath);

            return fileContents;

        } catch (Exception e) {
            return null;
        }

    }

    public boolean cacheFileExists(String fileName) {
        final Path cacheFilePath = cacheRoot.resolve(fileName);
        return Files.exists(cacheFilePath) && !Files.isDirectory(cacheFilePath);
    }

    private void createCacheDirIfEmpty() {
        if (!Files.exists(cacheRoot)) {
            log.info("Cache does not exist. Creating cache directory");
            if (!cacheRoot.toFile().mkdirs()) {
                throw new StacheException("Error creating cache directory");
            }
        }
    }


    private static Path resolveCachePath(StacheConfiguration stacheConfiguration, Class<?> mockType, Optional<String> testMethodName) {

        final CacheNamingStrategy cacheNamingStrategy = stacheConfiguration.getRunConfig().getCacheNamingStrategy();

        final String baseResourcesDirectory = stacheConfiguration.getRunConfig().getTestResourceDir();

        switch (cacheNamingStrategy) {
            case METHOD_SCOPED:

                if (testMethodName.isEmpty()) {
                    throw new StacheException("When using CacheNamingStrategy of METHOD_SCOPED, the test must be run using the StacheJunitRunner");
                }

                return Path.of(baseResourcesDirectory, stacheConfiguration.getRunConfig().getCacheDir(), testMethodName.get());
            case MOCK_SCOPED:
                return Path.of(baseResourcesDirectory, stacheConfiguration.getRunConfig().getCacheDir(), mockType.getSimpleName());
            case STATIC_SCOPED:
                return Path.of(baseResourcesDirectory, stacheConfiguration.getRunConfig().getCacheDir());
            case TEST_SUITE_SCOPED:
                throw new UnsupportedOperationException("CacheNamingStrategy of TEST_SUITE_SCOPED is not yet implemented");
            default:
                return Path.of(baseResourcesDirectory, stacheConfiguration.getRunConfig().getCacheDir());
        }

    }

}
