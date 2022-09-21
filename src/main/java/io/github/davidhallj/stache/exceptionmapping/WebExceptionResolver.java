package io.github.davidhallj.stache.exceptionmapping;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Resolves a String to a WebApplicationException and throws it
 *
 * TODO need some sort of default error handling for when there is an error we don't have a clause for
 */
public class WebExceptionResolver extends BaseExceptionResolver {

    private Map<String, ExceptionMapping> exceptionMappings;
    private List<String> exceptionNames;

    public WebExceptionResolver() {
        exceptionMappings = getExceptionMappings().stream().collect(Collectors.toMap(
                exceptionMapping -> resolveExceptionName(exceptionMapping.getCls()), Function.identity()));
        exceptionNames = getExceptionMappings().stream().map(exceptionMapping -> resolveExceptionName(exceptionMapping.getCls())).collect(Collectors.toList());
    }

    @Override
    public List<ExceptionMapping> getExceptionMappings() {
        return Arrays.asList(
                new ExceptionMapping<>(BadRequestException.class, BadRequestException::new, BadRequestException::new),
                new ExceptionMapping<>(NotFoundException.class, NotFoundException::new, NotFoundException::new),
                new ExceptionMapping<>(ServerErrorException.class, () -> new ServerErrorException(500), runtimeException -> new ServerErrorException("", 500, runtimeException)),
                new ExceptionMapping<>(InternalServerErrorException.class, InternalServerErrorException::new, runtimeException -> new InternalServerErrorException("", runtimeException))
        );
    }

    @Override
    public Optional<RuntimeException> extractException(String fileContents) {

        List<String> delimited = Arrays.asList(fileContents.split("\\s+"));

        final boolean hasException = exceptionNames.containsAll(delimited);

        if (!hasException) {
            return Optional.empty();
        }

        Stack<String> exceptionChain = new Stack<>();
        exceptionChain.addAll(delimited); // does this retain order?
        Collections.reverse(exceptionChain); // b/c exceptionChain pop starts from the end

        RuntimeException e = resolveException(exceptionChain);

        return Optional.ofNullable(e);
    }

    public String buildExceptionChain(Throwable e) {
        String s =  resolveExceptionName(e.getClass());
        if (e.getCause() != null) {
            s += " " + buildExceptionChain(e.getCause());
        }
        return s;
    }

    // Throwable?
    private RuntimeException resolveException(Stack<String> exceptionChain) {

        if (exceptionChain.isEmpty()) {
            return null; // or throw error? should never get here
        }

        final String current = exceptionChain.pop();

        final ExceptionMapping<?> exceptionMapping = exceptionMappings.get(current);

        if (exceptionChain.isEmpty()) {
            return exceptionMapping.instantiate();
        } else {
            return exceptionMapping.wrap(resolveException(exceptionChain));
        }

    }

}
