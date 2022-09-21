package io.github.davidhallj.stache.core;

import io.github.davidhallj.stache.config.StacheConfiguration;
import io.github.davidhallj.stache.proxy.CachingInvocationHandler;
import io.github.davidhallj.stache.proxy.StacheProxyImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class CachingProxyFactory {

    private StacheConfiguration stacheConfiguration;

    public <T> T buildCachingProxy(Object realImpl, Class<T> cls) {
        final CachingInvocationHandler cachingInvocationHandler = new CachingInvocationHandler(stacheConfiguration, realImpl, cls);

        StacheProxyImpl<T> stacheProxy = new StacheProxyImpl<>(cls, cachingInvocationHandler);

        return stacheProxy.proxy();
    }

}
