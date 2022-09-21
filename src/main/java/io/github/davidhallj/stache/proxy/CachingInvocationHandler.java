package io.github.davidhallj.stache.proxy;

import io.github.davidhallj.stache.config.StacheConfiguration;
import io.github.davidhallj.stache.config.StacheRunConfiguration;
import io.github.davidhallj.stache.exception.StacheException;
import io.github.davidhallj.stache.exceptionmapping.ExceptionResolver;
import io.github.davidhallj.stache.util.StacheCache;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * TODO implement a pattern for exception handling
 */
@Slf4j
public class CachingInvocationHandler implements InvocationHandler {

    private final Object realServiceImpl;
    private final StacheConfiguration stacheConfiguration;
    private final StacheCache stacheCache;
    private final Gson gson;

    private int hits = 0;

    public CachingInvocationHandler(StacheConfiguration stacheConfiguration, Object realServiceImpl, Class<?> cls) {
        this.realServiceImpl = realServiceImpl;
        this.stacheConfiguration = stacheConfiguration;
        this.stacheCache = new StacheCache(stacheConfiguration, cls);
        this.gson = stacheConfiguration.getRunConfig().getGson();
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {

        final StacheRunConfiguration runConfig = stacheConfiguration.getRunConfig();
        final ExceptionResolver exceptionResolver = runConfig.getExceptionResolver();

        hits++;
        Object result = null;

        String methodName = m.getName();

        // This solves an issue where the debugger calls toString() on the Proxy (and so we end up caching the response)
        // Could also compare against the list of values of the actual method names on the interface so that we're
        // only intercepting the real methods
        if (m.getName().contains("toString")) {
            return null;
        }

        final List<String> argNames = Optional.ofNullable(args).map(Arrays::stream).map(CachingInvocationHandler::argToString).stream().toList();

        final String cacheFileName = String.format("%s%s.json", methodName, argNames.toString());

        switch (runConfig.getRunStrategy()) {
            case SMART_CACHE_MODE -> {
                log.debug("SMART_CACHE_MODE");

                if (stacheCache.cacheFileExists(cacheFileName)) {

                    result = stacheCache.readCacheFile(cacheFileName);

                    if (result == null) {
                        log.debug("An empty cache file was found");
                        throw new StacheException(String.format("An empty cache file was found for [%s]", cacheFileName));
                    }

                    exceptionResolver.handleException((String) result);

                    return gson.fromJson(result.toString(), m.getReturnType());
                } else {

                    m = realServiceImpl.getClass().getMethod(methodName, m.getParameterTypes());

                    // If this is called using the real service, result is implicitly of the correct type
                    try {
                        result = m.invoke(realServiceImpl, args);
                    } catch (InvocationTargetException e) {

                        stacheCache.writeCacheFile(cacheFileName, exceptionResolver.buildExceptionChain(e.getTargetException()));
                        throw e.getTargetException();
                    }

                    stacheCache.writeCacheFile(cacheFileName, result == null ? "" : gson.toJson(result));

                    return result;
                }

            }
            case READ_ONLY_MODE -> {
                log.debug("READ_ONLY_MODE");
                result = stacheCache.readCacheFile(cacheFileName);

                if (result == null) {
                    throw new StacheException(String.format("Running in READ_ONLY mode but the matching cache file [%s] is not found", cacheFileName));
                }

                exceptionResolver.handleException((String) result);

                return gson.fromJson(result.toString(), m.getReturnType());
            }
            case DEV_MODE -> {
                log.debug("DEV_MODE");

                m = realServiceImpl.getClass().getMethod(methodName, m.getParameterTypes());

                try {
                    return m.invoke(realServiceImpl, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }

            }
            default -> {
                throw new StacheException("Invalid RunConfig");
            }
        }


    }

    private static String argToString(Object obj) {

        // TODO why not just check if the Class is of type string itself?
        if (obj.getClass().getTypeName().contains("String")) {
            return obj.toString();
        } else {
            // in the case of objects, we don't want to create new files each and every time , so just use the class name
            // so it can be reused. This could cause some issues down the road if you want to test difference inside
            // these object parameters, so this will need to be revisited
            return String.format("[%s]", obj.getClass().getName());
        }

    }


}
