package io.github.davidhallj.stache;

import io.github.davidhallj.stache.annotations.JaxrsStache;
import io.github.davidhallj.stache.config.Defaults;
import io.github.davidhallj.stache.config.RunStrategy;
import io.github.davidhallj.stache.core.StacheStaticContext;
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

import static io.github.davidhallj.stache.jaxrs.JaxrsTestUtils.buildServerAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(StacheJunitExtension.class)
public class DevModeAnnotationTest {

    private static final Path CACHE_ROOT = Path.of(Defaults.MAVEN_TEST_RESOURCES, Defaults.CACHE_DIR);

    @JaxrsStache(url = "http://0.0.0.0:8181/services/hello", runConfig = RunStrategy.DEV_MODE)
    private HelloResource helloResourceDevMode;

    @BeforeAll
    public static void classSetup() {
        final HelloResourceImpl impl = new HelloResourceImpl();
        final Server server = StacheStaticContext.JAXRS_FACTORY.createJaxrsServer(JaxrsTestUtils.buildServerAddress("hello"), HelloResource.class, impl);

        // Baseline -> no files in cache
        assertThat(Files.exists(CACHE_ROOT)).isFalse();

    }

    @AfterAll
    public static void classTeardown() {
        assertThat(Files.exists(CACHE_ROOT)).isFalse();
    }

    @Test
    void run() {
        final Greeting greeting1 = helloResourceDevMode.greet();
        final Greeting greeting2 = helloResourceDevMode.greet();
        final Greeting greeting3 = helloResourceDevMode.greet();

        assertThat(greeting1.getId()).isEqualTo(1);
        assertThat(greeting2.getId()).isEqualTo(2);
        assertThat(greeting3.getId()).isEqualTo(3);

    }

    @Test
    void willThrowServerErrorException() {
        assertThrows(InternalServerErrorException.class, () -> {
            helloResourceDevMode.willThrowServerErrorException();
        });
    }

    @Test
    void willThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> {
            helloResourceDevMode.willThrowBadRequestException();
        });
    }

}
