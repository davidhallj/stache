package io.github.davidhallj.stache.jaxrs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public final class Greeting {

    private final Integer id;
    private final String greeting;

}