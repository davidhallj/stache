package io.github.davidhallj.stache.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JaxrsStacheTestContextTest {

    @Test
    void builderDefaults() {

        StacheTestContext stacheTestContext = StacheTestContext.builder().build();

        assertThat(stacheTestContext.getTestMethodName()).isEmpty();


    }

}