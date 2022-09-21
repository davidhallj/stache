package io.github.davidhallj.stache.exceptionmapping;

public interface ExceptionResolver {

    void handleException(String fileContents);

    String buildExceptionChain(Throwable e);

}
