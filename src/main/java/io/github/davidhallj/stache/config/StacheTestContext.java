package io.github.davidhallj.stache.config;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
public class StacheTestContext {

    @Builder.Default
    private Optional<String> testMethodName = Optional.empty();

    @Builder
    public StacheTestContext(String testMethodName) {
        this.testMethodName = Optional.ofNullable(testMethodName);
    }
    private StacheTestContext() {
        this.testMethodName = Optional.empty();
    }

}

