package io.github.davidhallj.stache.exceptionmapping;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor
public class ExceptionMapping<T extends RuntimeException> {

    @Getter
    private Class<T> cls;
    private Supplier<T> instanceSupplier;
    private Function<RuntimeException, T> runtimeExceptionResolver;

    public T instantiate() {
        return instanceSupplier.get();
    }

    public T wrap(RuntimeException runtimeException) {
        return runtimeExceptionResolver.apply(runtimeException);
    }

}
