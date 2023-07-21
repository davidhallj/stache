package io.github.davidhallj.stache.core;

import io.github.davidhallj.stache.annotations.JaxrsStache;
import io.github.davidhallj.stache.annotations.Stache;
import io.github.davidhallj.stache.config.StacheConfiguration;
import io.github.davidhallj.stache.exception.StacheException;
import io.github.davidhallj.stache.util.ReflectionHelper;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Slf4j
public class StacheAnnotationsProcessor {

    public void process(Class<?> clazz, Object testInstance, String junitTestMethodName) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            for(Annotation annotation : field.getAnnotations()) {

                if (annotation.annotationType() == JaxrsStache.class) {

                    JaxrsStache jaxrsStacheAnnotation = (JaxrsStache) annotation;

                    final String uri = jaxrsStacheAnnotation.url();

                    if (uri == null || uri.isEmpty()) {
                        throw new StacheException("JaxrsStache annotation requires a url to be supplied");
                    }

                    final StacheConfiguration config = StacheConfiguration.create(jaxrsStacheAnnotation.runConfig(), jaxrsStacheAnnotation.advanced(), junitTestMethodName);

                    final Object jaxrsClient = config.getRunConfig().getJaxrsFactory().createJaxrsProxy(uri, field.getType());

                    Object cachingProxy = buildCachingProxy(
                            config,
                            jaxrsClient,
                            field.getType(),
                            junitTestMethodName
                    );

                    try {
                        ReflectionHelper.setField(testInstance, field, cachingProxy);
                    } catch (Exception e) {
                        throw new StacheException("Problems setting field " + field.getName() + " annotated with " + annotation, e);
                    }
                }

                if (annotation.annotationType() == Stache.class) {

                    Stache stacheAnnotation = (Stache) annotation;

                    final StacheConfiguration config = StacheConfiguration.create(stacheAnnotation.runConfig(), stacheAnnotation.advanced(), junitTestMethodName);

                    try {

                        field.setAccessible(true);

                        Object realImpl = field.get(testInstance);

                        if (realImpl == null) {
                            throw new StacheException("@Cached annotation requires you to provide a value for the field");
                        }

                        Object cachingProxy = buildCachingProxy(
                                config,
                                realImpl,
                                field.getType(),
                                junitTestMethodName
                        );

                        ReflectionHelper.setField(testInstance, field, cachingProxy);
                    } catch (Exception e) {
                        throw new RuntimeException("Problems setting field " + field.getName() + " annotated with " + annotation, e);
                    }

                }

            }
            field.setAccessible(false);
        }

    }

    public static Object buildCachingProxy(StacheConfiguration stacheConfiguration, Object realImpl, Class<?> fieldType, String junitTestMethodName) {
        return CachingProxyBuilder.buildCachingProxy(stacheConfiguration, realImpl, fieldType);
    }

    public void throwIfAlreadyAssigned(Field field, boolean alreadyAssigned) {
        if (alreadyAssigned) {
            throw new StacheException(String.format("Field %s already assigned to", field.getName()));
        }
    }

}
