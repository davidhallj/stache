package io.github.davidhallj.stache.jaxrs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.activation.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Provider
@Produces({MediaType.APPLICATION_JSON, "application/*+json"})
@Consumes({MediaType.APPLICATION_JSON, "application/*+json"})
public class GsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    private static final List<Class<?>> UNSUPPORTED = List.of(
            String.class,
            Reader.class,
            byte[].class,
            InputStream.class,
            StreamingOutput.class,
            File.class,
            DataSource.class
    );

    //private final GsonService gsonService;
    private final Gson gson;

    public GsonProvider(Gson gson) {
        this.gson = gson;
    }

    public GsonProvider() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private static boolean isSupported(Class<?> type) {
        return UNSUPPORTED.stream().noneMatch(unsupported -> unsupported.isAssignableFrom(type));
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupported(type);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        final InputStreamReader streamReader = new InputStreamReader(entityStream, StandardCharsets.UTF_8);
        return gson.fromJson(streamReader, resolveType(type, genericType));
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupported(type);
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        entityStream.write(gson.toJson(o).getBytes(StandardCharsets.UTF_8));
        entityStream.flush();
    }

    private Type resolveType(Class<?> type, Type genericType) {
        if (notEqual(type, genericType)) {
            return genericType;
        } else {
            return type;
        }
    }

    public static boolean notEqual(Object object1, Object object2) {
        return !equals(object1, object2);
    }

    public static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        } else {
            return object1 != null && object2 != null ? object1.equals(object2) : false;
        }
    }

}