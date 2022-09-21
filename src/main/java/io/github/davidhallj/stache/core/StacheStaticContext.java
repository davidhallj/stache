package io.github.davidhallj.stache.core;

import io.github.davidhallj.stache.jaxrs.JaxrsFactory;
import io.github.davidhallj.stache.jaxrs.JaxrsFactoryImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated
public class StacheStaticContext {

    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static final JaxrsFactory JAXRS_FACTORY = new JaxrsFactoryImpl();

}
