package fr.epita.assistant.jws.presentation.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class HelloWorldEndpoint {
    @GET @Path("/")
    public String helloWorld() {
        return "Hello World!";
    }

    @GET @Path("/{name}")
    public String helloWorld(@PathParam("name") String name) {
        return "Hello " + name + "!";
    }
    @GET @Path("/coucou/{name}")
    public String coucouName(@PathParam("name") String name)
    {
        return "Coucou " + name + "!!!!!";
    }

}