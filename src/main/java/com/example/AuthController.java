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
    public String login(@FormParam("name") String name, @FormParam("password") String password) {
        try {
            if (name == null || password == null || name.trim().isEmpty() || password.trim().isEmpty()) {
                return login.data("error", true).render();
            }
            
            User user = User.find("name", name.trim()).firstResult();
            if (user != null && user.password.equals(password.trim())) {
                return dashboard.data("users", User.listAll()).render();
            }
            return login.data("error", true).render();
        } catch (Exception e) {
            return login.data("error", true).render();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public String register(@FormParam("name") String name, @FormParam("age") String ageStr, @FormParam("email") String email, @FormParam("password") String password) {
        try {
            if (name == null || email == null || password == null || ageStr == null || 
                name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty() || ageStr.trim().isEmpty()) {
                return register.data("error", true).render();
            }
            
            Integer age = Integer.parseInt(ageStr.trim());
            if (age < 1 || age > 120) {
                return register.data("error", true).render();
            }
            
            if (User.findByEmail(email.trim()) != null) {
                return register.data("error", true).render();
            }
            
            User user = new User(name.trim(), age, email.trim(), password.trim());
            user.persist();
            return dashboard.data("users", User.listAll()).render();
        } catch (Exception e) {
            return register.data("error", true).render();
        }
    }

    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    public String dashboard() {
        return dashboard.data("users", User.listAll()).render();
    }
}