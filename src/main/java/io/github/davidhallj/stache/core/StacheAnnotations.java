package io.github.davidhallj.stache.core;

import io.github.davidhallj.stache.exception.StacheException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StacheAnnotations {

    public static void init(Object testClass) {
       init(testClass, null);
    }

    public static void init(Object testClass, String methodName) {

        if (testClass == null) {
            throw new StacheException("testClass cannot be null");
        } else {
            final StacheAnnotationsProcessor stacheAnnotationsProcessor = new StacheAnnotationsProcessor();
            stacheAnnotationsProcessor.process(testClass.getClass(), testClass, methodName);
        }

    }

}
