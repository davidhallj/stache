package io.github.davidhallj.stache.exceptionmapping;

import java.util.List;
import java.util.Optional;

public abstract class BaseExceptionResolver implements ExceptionResolver {

    protected abstract List<ExceptionMapping> getExceptionMappings();

    protected abstract Optional<RuntimeException> extractException(String fileContents);

    protected String resolveExceptionName(Class re) {
        return re.getCanonicalName();
    }

    @Override
    public final void handleException(String fileContents) {
        extractException(fileContents).ifPresent(e -> {throw e;});
    }
}
