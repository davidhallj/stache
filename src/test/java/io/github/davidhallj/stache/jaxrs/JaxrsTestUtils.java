package io.github.davidhallj.stache.jaxrs;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JaxrsTestUtils {

    public static String buildServerAddress(String address) {
        return buildServerAddress(address, getPort());
    }

    public static String buildServerAddress(String address, int port) {
        return String.format("http://0.0.0.0:%d/services/%s", port, address);
    }

    public static int getPort() {
        return 8181;
    }

}
