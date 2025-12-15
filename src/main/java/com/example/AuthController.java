package com.example;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/auth")
public class AuthController {

    @Inject
    Template login;
    
    @Inject
    Template register;
    
    @Inject
    Template dashboard;

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance loginPage() {
        return login.instance();
    }

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance registerPage() {
        return register.instance();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public Object login(@FormParam("email") String email, @FormParam("password") String password) {
        User user = User.findByEmail(email);
        if (user != null && user.password.equals(password)) {
            return Response.seeOther(URI.create("/dashboard")).build();
        }
        return login.data("error", true);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public Object register(@FormParam("name") String name, @FormParam("email") String email, @FormParam("password") String password) {
        if (User.findByEmail(email) != null) {
            return register.data("error", true);
        }
        User user = new User(name, email, password);
        user.persist();
        return Response.seeOther(URI.create("/dashboard")).build();
    }

    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance dashboard() {
        return dashboard.data("users", User.listAll());
    }
}