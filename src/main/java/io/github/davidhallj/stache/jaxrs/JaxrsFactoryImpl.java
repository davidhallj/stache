package io.github.davidhallj.stache.jaxrs;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.transport.common.gzip.GZIPFeature;

import java.util.List;

@Slf4j
public class JaxrsFactoryImpl implements JaxrsFactory {

    private final GsonProvider gsonProvider;

    public JaxrsFactoryImpl() {
        gsonProvider = new GsonProvider();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object createJaxrsProxy(String address, Class serviceInterface) {
        log.info("Creating JAX-RS {} client at {} ...", serviceInterface.getName(), address);
        return JAXRSClientFactory.create(address, serviceInterface, getClientPlugins(), cxfFeatures(), null);
    }

    @Override
    public Server createJaxrsServer(String address, Class serviceInterface, Object serviceImpl) {
        log.info("Creating JAX-RS {} server at {} ...", serviceInterface.getName(), address);
        final JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setBus(BusFactory.getDefaultBus(true));
        sf.setFeatures(cxfFeatures());
        sf.setProviders(getServerPlugins());
        sf.setServiceBean(serviceImpl);
        sf.setAddress(address);
        sf.setStaticSubresourceResolution(true);
        return sf.create();
    }

    private List<Object> getClientPlugins() {
        return List.of(gsonProvider);
    }

    private List<Object> getServerPlugins() {
        return List.of(gsonProvider);
    }

    private List<Feature> cxfFeatures() {
        return List.of(new GZIPFeature(), new LoggingFeature());
    }

}
