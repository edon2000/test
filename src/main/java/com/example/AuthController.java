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
            return login.data("error", false).render();
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
            return register.data("error", false).render();
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

    // Advanced Login Page
    @GET
    @Path("/advanced-login")
    @Produces(MediaType.TEXT_HTML)
    public String advancedLoginPage() {
        return "<html><head><title>Advanced Login</title><link rel='stylesheet' href='/styles.css'></head><body>" +
               "<div class='auth-container'>" +
               "<h1>🔐 Advanced Login</h1>" +
               "<form method='post' action='/advanced-login' class='auth-form'>" +
               "<input type='text' name='username' placeholder='Username or Email' required minlength='3'>" +
               "<input type='password' name='password' placeholder='Password' required minlength='6'>" +
               "<div class='checkbox-group'><input type='checkbox' name='remember' id='remember'><label for='remember'>Remember me</label></div>" +
               "<button type='submit' class='btn-loading'>🚀 Login</button>" +
               "</form>" +
               "<div class='auth-links'>" +
               "<a href='/advanced-register'>Create Account</a> | " +
               "<a href='/forgot-password'>Forgot Password?</a>" +
               "</div>" +
               "</div></body></html>";
    }

    // Advanced Register Page
    @GET
    @Path("/advanced-register")
    @Produces(MediaType.TEXT_HTML)
    public String advancedRegisterPage() {
        return "<html><head><title>Advanced Register</title><link rel='stylesheet' href='/styles.css'></head><body>" +
               "<div class='auth-container'>" +
               "<h1>✨ Create Account</h1>" +
               "<form method='post' action='/advanced-register' class='auth-form'>" +
               "<input type='text' name='firstName' placeholder='First Name' required minlength='2'>" +
               "<input type='text' name='lastName' placeholder='Last Name' required minlength='2'>" +
               "<input type='email' name='email' placeholder='Email Address' required>" +
               "<input type='number' name='age' placeholder='Age' required min='13' max='120'>" +
               "<input type='password' name='password' placeholder='Password' required minlength='8' pattern='(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}' title='Password must contain at least 8 characters with uppercase, lowercase and number'>" +
               "<input type='password' name='confirmPassword' placeholder='Confirm Password' required>" +
               "<select name='country' required><option value=''>Select Country</option><option value='US'>United States</option><option value='UK'>United Kingdom</option><option value='CA'>Canada</option><option value='AU'>Australia</option></select>" +
               "<div class='checkbox-group'><input type='checkbox' name='terms' id='terms' required><label for='terms'>I agree to Terms & Conditions</label></div>" +
               "<div class='checkbox-group'><input type='checkbox' name='newsletter' id='newsletter'><label for='newsletter'>Subscribe to newsletter</label></div>" +
               "<button type='submit' class='btn-loading'>🎉 Create Account</button>" +
               "</form>" +
               "<div class='auth-links'>" +
               "<a href='/advanced-login'>Already have account? Login</a>" +
               "</div>" +
               "</div></body></html>";
    }

    // Advanced Login Processing
    @POST
    @Path("/advanced-login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public String advancedLogin(@FormParam("username") String username, @FormParam("password") String password, @FormParam("remember") String remember) {
        try {
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                return "<html><body><h1>❌ VALIDATION ERROR</h1><p>Username and password are required</p><a href='/advanced-login'>Try Again</a></body></html>";
            }
            
            if (password.length() < 6) {
                return "<html><body><h1>❌ VALIDATION ERROR</h1><p>Password must be at least 6 characters</p><a href='/advanced-login'>Try Again</a></body></html>";
            }
            
            // Try login by name or email
            User user = User.find("name = ?1 OR email = ?1", username.trim()).firstResult();
            if (user != null && user.password.equals(password.trim())) {
                String rememberStatus = remember != null ? "(Remember me enabled)" : "";
                return "<html><head><link rel='stylesheet' href='/styles.css'></head><body><div class='success-container'><h1>🎉 Login Successful!</h1><p>Welcome back, " + user.name + "! " + rememberStatus + "</p><a href='/dashboard' class='btn btn-primary'>Go to Dashboard</a></div></body></html>";
            }
            return "<html><body><h1>❌ LOGIN FAILED</h1><p>Invalid username or password</p><a href='/advanced-login'>Try Again</a></body></html>";
        } catch (Exception e) {
            return "<html><body><h1>❌ LOGIN ERROR</h1><p>" + e.getMessage() + "</p><a href='/advanced-login'>Try Again</a></body></html>";
        }
    }

    // Advanced Register Processing
    @POST
    @Path("/advanced-register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public String advancedRegister(@FormParam("firstName") String firstName, @FormParam("lastName") String lastName, 
                                 @FormParam("email") String email, @FormParam("age") String ageStr, 
                                 @FormParam("password") String password, @FormParam("confirmPassword") String confirmPassword,
                                 @FormParam("country") String country, @FormParam("terms") String terms, @FormParam("newsletter") String newsletter) {
        try {
            // Validation
            if (firstName == null || lastName == null || email == null || password == null || confirmPassword == null || ageStr == null || country == null) {
                return "<html><body><h1>❌ VALIDATION ERROR</h1><p>All fields are required</p><a href='/advanced-register'>Try Again</a></body></html>";
            }
            
            if (terms == null) {
                return "<html><body><h1>❌ VALIDATION ERROR</h1><p>You must agree to Terms & Conditions</p><a href='/advanced-register'>Try Again</a></body></html>";
            }
            
            if (!password.equals(confirmPassword)) {
                return "<html><body><h1>❌ VALIDATION ERROR</h1><p>Passwords do not match</p><a href='/advanced-register'>Try Again</a></body></html>";
            }
            
            if (password.length() < 8) {
                return "<html><body><h1>❌ VALIDATION ERROR</h1><p>Password must be at least 8 characters</p><a href='/advanced-register'>Try Again</a></body></html>";
            }
            
            Integer age = Integer.parseInt(ageStr.trim());
            if (age < 13 || age > 120) {
                return "<html><body><h1>❌ VALIDATION ERROR</h1><p>Age must be between 13 and 120</p><a href='/advanced-register'>Try Again</a></body></html>";
            }
            
            if (User.findByEmail(email.trim()) != null) {
                return "<html><body><h1>❌ REGISTRATION ERROR</h1><p>Email already exists</p><a href='/advanced-register'>Try Again</a></body></html>";
            }
            
            String fullName = firstName.trim() + " " + lastName.trim();
            User user = new User(fullName, age, email.trim(), password.trim());
            user.persist();
            
            String newsletterStatus = newsletter != null ? "You're subscribed to our newsletter!" : "";
            return "<html><head><link rel='stylesheet' href='/styles.css'></head><body><div class='success-container'><h1>🎉 Registration Successful!</h1><p>Welcome " + fullName + " from " + country + "!</p><p>" + newsletterStatus + "</p><a href='/advanced-login' class='btn btn-primary'>Login Now</a></div></body></html>";
        } catch (Exception e) {
            return "<html><body><h1>❌ REGISTRATION ERROR</h1><p>" + e.getMessage() + "</p><a href='/advanced-register'>Try Again</a></body></html>";
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