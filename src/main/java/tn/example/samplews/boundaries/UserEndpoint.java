package tn.example.samplews.boundaries;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.example.samplews.controllers.UserManager;
import tn.example.samplews.entities.User;

import java.util.List;
import java.util.logging.Logger;

@Path("/users")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserEndpoint {
    @Inject
    private Logger log;
    @EJB
    private UserManager userManager;

    @GET
    public Response fetchAllUsers(){
        GenericEntity<List<User>> users = new GenericEntity<>(userManager.findAll()){};
        return Response.ok().entity(users).build();
    }


}
