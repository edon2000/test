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
    public String loginPage() {
        return login.render();
    }

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public String registerPage() {
        return register.render();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public Object login(@FormParam("name") String name, @FormParam("password") String password) {
        User user = User.find("name", name).firstResult();
        if (user != null && user.password.equals(password)) {
            return Response.seeOther(URI.create("/dashboard")).build();
        }
        return login.data("error", true).render();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public Object register(@FormParam("name") String name, @FormParam("age") Integer age, @FormParam("email") String email, @FormParam("password") String password) {
        if (User.findByEmail(email) != null) {
            return register.data("error", true).render();
        }
        User user = new User(name, age, email, password);
        user.persist();
        return Response.seeOther(URI.create("/dashboard")).build();
    }

    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    public String dashboard() {
        return dashboard.data("users", User.listAll()).render();
    }
}