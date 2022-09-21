package io.github.davidhallj.stache.core;

import io.github.davidhallj.stache.config.StacheConfiguration;
import io.github.davidhallj.stache.proxy.CachingInvocationHandler;
import io.github.davidhallj.stache.proxy.StacheProxyImpl;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CachingProxyBuilder {

    public static Object buildCachingProxy(StacheConfiguration config, Object realImpl, Class<?> cls) {

        final CachingInvocationHandler cachingInvocationHandler = new CachingInvocationHandler(config, realImpl, cls);

        StacheProxyImpl<?> stacheProxy = new StacheProxyImpl<>(cls, cachingInvocationHandler);

        return stacheProxy.proxy();
    }

}
