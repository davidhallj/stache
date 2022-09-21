package io.github.davidhallj.stache;

import io.github.davidhallj.stache.annotations.JaxrsStache;
import io.github.davidhallj.stache.config.Defaults;
import io.github.davidhallj.stache.core.StacheStaticContext;
import io.github.davidhallj.stache.jaxrs.Greeting;
import io.github.davidhallj.stache.jaxrs.HelloResource;
import io.github.davidhallj.stache.jaxrs.HelloResourceImpl;
import io.github.davidhallj.stache.junit.StacheJunitExtension;
import io.github.davidhallj.stache.util.StacheTestUtil;
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
public class SmartCacheModeAnnotationTest {

    private static final Path CACHE_ROOT = Path.of(Defaults.MAVEN_TEST_RESOURCES, Defaults.CACHE_DIR);

    static final boolean runCleanupAndBaselines = true;

    @JaxrsStache(url = "http://0.0.0.0:8181/services/hello")
    private HelloResource helloResourceSmartCacheMode;

    @BeforeAll
    public static void classSetup() {
        final HelloResourceImpl impl = new HelloResourceImpl();
        final Server server = StacheStaticContext.JAXRS_FACTORY.createJaxrsServer(buildServerAddress("hello"), HelloResource.class, impl);


        if (runCleanupAndBaselines) {
            // Baseline -> no files in cache
            //assertThat(CACHE_ROOT.toFile().listFiles()).isEmpty();
            assertThat(Files.exists(CACHE_ROOT)).isFalse();
        }

    }

    @AfterAll
    public static void classTeardown() {
        if (runCleanupAndBaselines) {
            StacheTestUtil.deleteDirectory(CACHE_ROOT);
        }
    }

    @Test
    void run() {
        final Greeting greeting1 = helloResourceSmartCacheMode.greet();
        final Greeting greeting2 = helloResourceSmartCacheMode.greet();
        final Greeting greeting3 = helloResourceSmartCacheMode.greet();

        assertThat(greeting1.getId()).isEqualTo(1);
        assertThat(greeting2.getId()).isEqualTo(greeting1.getId());
        assertThat(greeting3.getId()).isEqualTo(greeting2.getId());

    }

    @Test
    void willThrowServerErrorException() {
        assertThrows(InternalServerErrorException.class, () -> {
            helloResourceSmartCacheMode.willThrowServerErrorException();
        });
    }

    @Test
    void willThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> {
            helloResourceSmartCacheMode.willThrowBadRequestException();
        });
    }

}
