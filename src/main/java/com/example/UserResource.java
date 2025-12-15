package com.example;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.net.URI;

@Path("/")
public class UserResource {

    @Inject
    Template index;
    
    @Inject
    Template results;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String home() {
        return index.render();
    }



    @GET
    @Path("/results")
    @Produces(MediaType.TEXT_HTML)
    public String getResults() {
        return results.data("users", User.listAll()).render();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        return String.valueOf(User.count());
    }
}