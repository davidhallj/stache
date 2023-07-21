package io.github.davidhallj.stache;

import io.github.davidhallj.stache.annotations.Stache;
import io.github.davidhallj.stache.config.Defaults;
import io.github.davidhallj.stache.core.StacheStaticContext;
import io.github.davidhallj.stache.jaxrs.Greeting;
import io.github.davidhallj.stache.jaxrs.HelloResource;
import io.github.davidhallj.stache.jaxrs.HelloResourceImpl;
import io.github.davidhallj.stache.jaxrs.JaxrsFactoryImpl;
import io.github.davidhallj.stache.junit.StacheJunitExtension;
import io.github.davidhallj.stache.util.StacheTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.davidhallj.stache.jaxrs.JaxrsTestUtils.buildServerAddress;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(StacheJunitExtension.class)
public class StacheAnnotationTest {

    private static final Path CACHE_ROOT = Path.of(Defaults.MAVEN_TEST_RESOURCES, Defaults.CACHE_DIR);

    @Stache
    HelloResource helloJaxrsResource = (HelloResource) new JaxrsFactoryImpl().createJaxrsProxy("http://0.0.0.0:8181/services/hello", HelloResource.class);

    @Stache
    HelloResource helloResourceNoRest = new HelloResourceImpl();

    static final boolean runCleanupAndBaselines = true;

    @BeforeAll
    public static void classSetup() {
        final HelloResourceImpl impl = new HelloResourceImpl();
        final Server server = StacheStaticContext.JAXRS_FACTORY.createJaxrsServer(buildServerAddress("hello"), HelloResource.class, impl);


        if (runCleanupAndBaselines) {
            // Baseline -> no files in cache
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
    void cacheFlowNoRest() {

        final Greeting greeting1 = helloResourceNoRest.greet();

        assertThat(greeting1.getId()).isNotNull();

        final Greeting greeting2 = helloResourceNoRest.greet();
        assertThat(greeting2.getId()).isEqualTo(greeting1.getId());

        final Greeting greeting3 = helloResourceNoRest.greet();
        assertThat(greeting3.getId()).isEqualTo(greeting1.getId());

        final Greeting greeting4 = helloResourceNoRest.greet();
        assertThat(greeting4.getId()).isEqualTo(greeting1.getId());

    }

    @Test
    void explicitRestImpl() {
        final Greeting greeting1 = helloJaxrsResource.greet();

        assertThat(greeting1.getId()).isNotNull();

        final Greeting greeting2 = helloJaxrsResource.greet();
        assertThat(greeting2.getId()).isEqualTo(greeting1.getId());

        final Greeting greeting3 = helloJaxrsResource.greet();
        assertThat(greeting3.getId()).isEqualTo(greeting1.getId());

        final Greeting greeting4 = helloJaxrsResource.greet();
        assertThat(greeting4.getId()).isEqualTo(greeting1.getId());
    }

}
