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
    public Response home() {
        return Response.seeOther(URI.create("/auth/login")).build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public String register(@FormParam("name") String name) {
        boolean success = false;
        if (name != null && !name.trim().isEmpty()) {
            User user = new User(name.trim());
            user.persist();
            success = user.isPersistent();
        }
        return results.data("users", User.listAll()).data("success", success).render();
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