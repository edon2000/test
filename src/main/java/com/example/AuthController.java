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
    @Transactional
    public Response login(@FormParam("email") String email, @FormParam("password") String password) {
        User user = User.findByEmail(email);
        if (user != null && user.password.equals(password)) {
            return Response.seeOther(URI.create("/dashboard")).build();
        }
        return Response.seeOther(URI.create("/auth/login?error=1")).build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response register(@FormParam("name") String name, @FormParam("email") String email, @FormParam("password") String password) {
        if (User.findByEmail(email) != null) {
            return Response.seeOther(URI.create("/auth/register?error=1")).build();
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

@Path("/dashboard")
public class DashboardController {
    
    @Inject
    Template dashboard;
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return dashboard.data("users", User.listAll());
    }
}