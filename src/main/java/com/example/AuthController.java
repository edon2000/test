package com.example;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/")
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
        try {
            return login.render();
        } catch (io.quarkus.qute.TemplateException e) {
            return "<html><body><h1>TEMPLATE ERROR</h1><p>Login template failed: " + e.getMessage() + "</p><form method='post' action='/login'><input name='name' placeholder='Name' required><input name='password' type='password' placeholder='Password' required><button type='submit'>Login</button></form></body></html>";
        } catch (Exception e) {
            return "<html><body><h1>UNKNOWN ERROR</h1><p>Login page error: " + e.getClass().getSimpleName() + " - " + e.getMessage() + "</p></body></html>";
        }
    }

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public String registerPage() {
        try {
            return register.render();
        } catch (io.quarkus.qute.TemplateException e) {
            return "<html><body><h1>TEMPLATE ERROR</h1><p>Register template failed: " + e.getMessage() + "</p><form method='post' action='/register'><input name='name' placeholder='Name' required><input name='age' type='number' placeholder='Age' required><input name='email' type='email' placeholder='Email' required><input name='password' type='password' placeholder='Password' required><button type='submit'>Register</button></form></body></html>";
        } catch (Exception e) {
            return "<html><body><h1>UNKNOWN ERROR</h1><p>Register page error: " + e.getClass().getSimpleName() + " - " + e.getMessage() + "</p></body></html>";
        }
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
        } catch (jakarta.persistence.PersistenceException e) {
            return "<html><body><h1>DATABASE ERROR</h1><p>Login database error: " + e.getMessage() + "</p><a href='/login'>Try Again</a></body></html>";
        } catch (NumberFormatException e) {
            return "<html><body><h1>VALIDATION ERROR</h1><p>Invalid input format: " + e.getMessage() + "</p><a href='/login'>Try Again</a></body></html>";
        } catch (io.quarkus.qute.TemplateException e) {
            return "<html><body><h1>TEMPLATE ERROR</h1><p>Login template error: " + e.getMessage() + "</p><a href='/login'>Try Again</a></body></html>";
        } catch (Exception e) {
            return "<html><body><h1>API ERROR</h1><p>Login API error: " + e.getClass().getSimpleName() + " - " + e.getMessage() + "</p><a href='/login'>Try Again</a></body></html>";
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
        } catch (jakarta.persistence.PersistenceException e) {
            return "<html><body><h1>DATABASE ERROR</h1><p>Registration database error: " + e.getMessage() + "</p><a href='/register'>Try Again</a></body></html>";
        } catch (NumberFormatException e) {
            return "<html><body><h1>VALIDATION ERROR</h1><p>Invalid age format: " + e.getMessage() + "</p><a href='/register'>Try Again</a></body></html>";
        } catch (io.quarkus.qute.TemplateException e) {
            return "<html><body><h1>TEMPLATE ERROR</h1><p>Register template error: " + e.getMessage() + "</p><a href='/register'>Try Again</a></body></html>";
        } catch (Exception e) {
            return "<html><body><h1>API ERROR</h1><p>Registration API error: " + e.getClass().getSimpleName() + " - " + e.getMessage() + "</p><a href='/register'>Try Again</a></body></html>";
        }
    }

    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public String dashboard() {
        try {
            return dashboard.data("users", User.listAll()).render();
        } catch (jakarta.persistence.PersistenceException e) {
            return "<html><body><h1>DATABASE ERROR</h1><p>Dashboard database error: " + e.getMessage() + "</p><a href='/'>Home</a></body></html>";
        } catch (io.quarkus.qute.TemplateException e) {
            return "<html><body><h1>TEMPLATE ERROR</h1><p>Dashboard template error: " + e.getMessage() + "</p><a href='/'>Home</a></body></html>";
        } catch (Exception e) {
            return "<html><body><h1>API ERROR</h1><p>Dashboard API error: " + e.getClass().getSimpleName() + " - " + e.getMessage() + "</p><a href='/'>Home</a></body></html>";
        }
    }
}