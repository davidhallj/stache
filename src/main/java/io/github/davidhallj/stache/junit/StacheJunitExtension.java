package io.github.davidhallj.stache.junit;

import io.github.davidhallj.stache.core.StacheAnnotations;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
@NoArgsConstructor
public class StacheJunitExtension implements BeforeEachCallback, BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        StacheAnnotations.init(
                extensionContext.getTestInstance().get(),
                extensionContext.getTestMethod().get().getName()
        );
    }

}
