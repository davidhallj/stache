package io.github.davidhallj.stache;

import io.github.davidhallj.stache.annotations.JaxrsStache;
import io.github.davidhallj.stache.config.Defaults;
import io.github.davidhallj.stache.config.RunStrategy;
import io.github.davidhallj.stache.core.StacheStaticContext;
import io.github.davidhallj.stache.exception.StacheException;
import io.github.davidhallj.stache.jaxrs.Greeting;
import io.github.davidhallj.stache.jaxrs.HelloResource;
import io.github.davidhallj.stache.jaxrs.HelloResourceImpl;
import io.github.davidhallj.stache.jaxrs.JaxrsTestUtils;
import io.github.davidhallj.stache.junit.StacheJunitExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(StacheJunitExtension.class)
public class ReadOnlyModeAnnotationTest {

    private static final Path CACHE_ROOT = Path.of(Defaults.MAVEN_TEST_RESOURCES, Defaults.CACHE_DIR);

    @JaxrsStache(url = "http://0.0.0.0:8181/services/hello", runConfig = RunStrategy.READ_ONLY_MODE)
    private HelloResource helloResourceReadOnlyMode;

    @JaxrsStache(url = "http://0.0.0.0:8181/services/hello")
    private HelloResource helloResourceSmartCacheMode;

    @BeforeAll
    public static void classSetup() {
        final HelloResourceImpl impl = new HelloResourceImpl();
        final Server server = StacheStaticContext.JAXRS_FACTORY.createJaxrsServer(JaxrsTestUtils.buildServerAddress("hello"), HelloResource.class, impl);

        // Baseline -> no files in cache
        assertThat(Files.exists(CACHE_ROOT)).isFalse();


    }

    @AfterAll
    public static void classTeardown() {
        //assertThat(Files.exists(CACHE_ROOT)).isFalse();
    }

    @Test
    void run() {

        assertThrows(StacheException.class, () -> {
            helloResourceReadOnlyMode.greet();
        });

        final Greeting baselineGreeting = helloResourceSmartCacheMode.greet();


        final Greeting greeting1 = helloResourceReadOnlyMode.greet();
        final Greeting greeting2 = helloResourceReadOnlyMode.greet();
        final Greeting greeting3 = helloResourceReadOnlyMode.greet();

        assertThat(greeting1.getId()).isEqualTo(baselineGreeting.getId());
        assertThat(greeting2.getId()).isEqualTo(baselineGreeting.getId());
        assertThat(greeting3.getId()).isEqualTo(baselineGreeting.getId());

    }

    @Test
    void willThrowServerErrorException() {

        assertThrows(StacheException.class, () -> {
            helloResourceReadOnlyMode.willThrowServerErrorException();
        });

        assertThrows(InternalServerErrorException.class, () -> {
            helloResourceSmartCacheMode.willThrowServerErrorException();
        });

        assertThrows(InternalServerErrorException.class, () -> {
            helloResourceReadOnlyMode.willThrowServerErrorException();
        });
    }

    @Test
    void willThrowBadRequestException() {

        assertThrows(StacheException.class, () -> {
            helloResourceReadOnlyMode.willThrowBadRequestException();
        });

        assertThrows(BadRequestException.class, () -> {
            helloResourceSmartCacheMode.willThrowBadRequestException();
        });

        assertThrows(BadRequestException.class, () -> {
            helloResourceReadOnlyMode.willThrowBadRequestException();
        });
    }

}
