package io.github.davidhallj.stache.config;

import io.github.davidhallj.stache.annotations.Advanced;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StacheConfiguration {

    private final StacheRunConfiguration runConfig;
    private final StacheTestContext testContext;

    public static StacheConfiguration create(RunStrategy runStrategy, Advanced advanced, String testMethodName) {

        // TODO validations

        final StacheTestContext stacheTestContext = StacheTestContext.builder()
                .testMethodName(testMethodName)
                .build();

        final StacheRunConfiguration stacheRunConfiguration = StacheRunConfiguration.builder()
                .runStrategy(runStrategy)
                .testResourceDir(advanced.resourcesDirectoryPath())
                .cacheDir(advanced.cacheDirectoryName())
                .cacheNamingStrategy(advanced.cacheNamingStrategy())
                .build();

        return StacheConfiguration.builder()
                .runConfig(stacheRunConfiguration)
                .testContext(stacheTestContext)
                .build();

    }

}
