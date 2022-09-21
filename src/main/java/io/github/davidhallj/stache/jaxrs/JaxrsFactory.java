package io.github.davidhallj.stache.jaxrs;

import org.apache.cxf.endpoint.Server;

public interface JaxrsFactory {

    Object createJaxrsProxy(String address, Class serviceInterface);

    Server createJaxrsServer(String address, Class serviceInterface, Object serviceImpl);

}
