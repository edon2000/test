package com.example;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

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