package io.github.davidhallj.stache.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface HelloResource {

    String url = JaxrsTestUtils.buildServerAddress("hello");

    @GET
    @Path("greet")
    Greeting greet();

    @GET
    @Path("/willThrow/ServerErrorException")
    void willThrowServerErrorException();

    @GET
    @Path("/willThrow/BadRequestException")
    void willThrowBadRequestException();

}
